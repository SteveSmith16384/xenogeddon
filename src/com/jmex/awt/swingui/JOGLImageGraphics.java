/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.awt.swingui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

import com.jme.image.Texture;
import com.jme.system.DisplaySystem;


/**
 * JOGL implementation of {@link ImageGraphics}.
 */
class JOGLImageGraphics extends ImageGraphicsBaseImpl {
    private JOGLImageGraphics( BufferedImage awtImage, byte[] data, Graphics2D delegate,
                               com.jme.image.Image image, Rectangle dirty,
                               int translationX, int translationY,
                               float scaleX, float scaleY, int mipMapCount,
                               ImageGraphicsBaseImpl mipMapChild, int mipMapLevel ) {
        super( awtImage, data, delegate, image, dirty, translationX, translationY, scaleX, scaleY,
                mipMapCount, mipMapChild, mipMapLevel );
    }

    protected JOGLImageGraphics( int width, int height, int paintedMipMapCount ) {
        this( width, height, paintedMipMapCount, 0, 1 );
    }

    private JOGLImageGraphics( int width, int height, int paintedMipMapCount, int mipMapLevel, float scale ) {
        super( width, height, paintedMipMapCount, mipMapLevel, scale );

        if ( paintedMipMapCount > 0 && ( width > 1 || height > 1 ) ) {
            if ( width < 2 ) {
                width = 2;
            }
            if ( height < 2 ) {
                height = 2;
            }
            mipMapChild = new JOGLImageGraphics( width / 2, height / 2, paintedMipMapCount, mipMapLevel + 1, scale * 0.5f );
        }
        setBackground( TRANSPARENT );   // Why is this JOGL specific?
    }

    public void update( Texture texture, boolean clean ) {
        final GL gl = GLU.getCurrentGL();
        final GLU glu = new GLU();
        boolean updateChildren = false;
        synchronized ( dirty ) {
            if ( !dirty.isEmpty() ) {
                dirty.grow( 2, 2 ); // to prevent antialiasing problems
            }
            Rectangle2D.intersect( dirty, getImageBounds(), dirty );

            if ( !this.dirty.isEmpty() ) {

                //debug: check if we already have an error from previous operations
                final int errorCode = gl.glGetError();
                if ( errorCode != GL.GL_NO_ERROR ) {
                    throw new GLException( glu.gluErrorString( errorCode ) );
                }

                boolean hasMipMaps = texture.getMinificationFilter().usesMipMapLevels();

                update();
                if ( !glTexSubImage2DSupported || ( hasMipMaps && paintedMipMapCount == 0 ) ) {

                    if ( !hasMipMaps ) {
                        DisplaySystem.getDisplaySystem().getRenderer()
                                .updateTextureSubImage(texture, 0, 0, image, 0,
                                        0, image.getWidth(), image.getHeight());
                    }
                    else {
                        // Remember what was previously bound.
                        idBuff.clear();
                        gl.glGetIntegerv(GL.GL_TEXTURE_BINDING_2D, idBuff);
                        int oldTex = idBuff.get();

                        gl.glBindTexture( GL.GL_TEXTURE_2D, texture.getTextureId() );
                        //set alignment to support images with  width % 4 != 0, as images are not aligned
                        gl.glPixelStorei( GL.GL_UNPACK_ALIGNMENT, 1 );

                        ByteBuffer data = image.getData(0);
                        data.rewind();
                        
                        glu.gluBuild2DMipmaps( GL.GL_TEXTURE_2D,
                                GL.GL_RGBA8, image
                                .getWidth(), image.getHeight(),
                                GL.GL_RGBA,
                                GL.GL_UNSIGNED_BYTE, data );
                        
                      // Rebind previous texture.
                      gl.glBindTexture(GL.GL_TEXTURE_2D, oldTex);
                    }
                    //debug: check if texture operations caused an error
                    final int errorCode3 = gl.glGetError();
                    if ( errorCode3 != GL.GL_NO_ERROR ) {
                        throw new GLException( glu.gluErrorString( errorCode3 ) );
                    }
                } else {
                    DisplaySystem.getDisplaySystem().getRenderer()
                            .updateTextureSubImage(texture, dirty.x, dirty.y,
                                    image, dirty.x, dirty.y, dirty.width,
                                    dirty.height);
                    final int errorCode2 = gl.glGetError();
                    if ( errorCode2 != GL.GL_NO_ERROR ) {

                        logger.warning( "Error updating dirty region: " + dirty
                                + " - "
                                + "falling back to updating whole image!" );
                        glTexSubImage2DSupported = false;
                        update( texture, clean );
                    }
                    updateChildren = mipMapChild != null;
                }
            }
        }
        if ( updateChildren ) {
            // delete lowest order bit to make position dividable by two
            dirty.x &= ~1;
            dirty.y &= ~1;
            // make size dividable by two
            if ( ( dirty.width & 1 ) != 0 ) {
                dirty.width++;
            }
            if ( ( dirty.height & 1 ) != 0 ) {
                dirty.height++;
            }

            int dx1 = (int) ( ( dirty.x - translation.x ) / scaleX );
            int dy1 = (int) ( ( dirty.y - translation.y ) / scaleY );
            int dx2 = (int) ( ( dirty.x + dirty.width - translation.x ) / scaleX );
            int dy2 = (int) ( ( dirty.y + dirty.height - translation.y ) / scaleY );
            int dw = dx2 - dx1;
            int dh = dy2 - dy1;
            mipMapChild.setClip( dx1, dy1, dw, dh );
            // draw image on the mip map child image but don't let them draw it on their children
            mipMapChild.delegate.clearRect( dx1, dy1, dw, dh );
            mipMapChild.delegate.drawImage( awtImage, dx1, dy1, dx2, dy2,
                    dirty.x, dirty.y, dirty.x + dirty.width, dirty.y + dirty.height, null );
            mipMapChild.makeDirty( dx1, dy1, dw, dh );

            mipMapChild.update( texture, clean );
        }
        if ( clean ) {
            this.dirty.width = 0;
        }
    }

    public Graphics create() {
        return new JOGLImageGraphics( awtImage, data, (Graphics2D) delegate.create(), image, dirty,
                translation.x, translation.y, scaleX, scaleY, paintedMipMapCount,
                mipMapChild != null && mipMapLevel < paintedMipMapCount - 1
                        ? (JOGLImageGraphics) mipMapChild.create() : null, mipMapLevel );
    }
}
