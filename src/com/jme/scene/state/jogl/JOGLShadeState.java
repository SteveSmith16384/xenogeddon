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

package com.jme.scene.state.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.jme.renderer.RenderContext;
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.jogl.records.ShadeStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>JOGLShadeState</code> subclasses the ShadeState class using the
 * JOGL API to access OpenGL to set the shade state.
 *
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @author Steve Vaughan - JOGL port
 * @version $Id: JOGLShadeState.java 4091 2009-01-21 19:01:20Z joshua.j.ellen $
 */
public class JOGLShadeState extends ShadeState {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor instantiates a new <code>JOGLShadeState</code> object.
     *
     */
    public JOGLShadeState() {
        super();
    }

    /**
     * <code>set</code> sets the OpenGL shade state to that specified by the
     * state.
     *
     * @see com.jme.scene.state.ShadeState#apply() ()
     */
    public void apply() {
        final GL gl = GLU.getCurrentGL();

        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        ShadeStateRecord record = (ShadeStateRecord) context.getStateRecord(StateType.Shade);
        context.currentStates[StateType.Shade.ordinal()] = this;

        // If not enabled, we'll use smooth
        int toApply = isEnabled() ? getGLShade() : GL.GL_SMOOTH;
        // only apply if we're different. Update record to reflect any changes.
        if (!record.isValid() || toApply != record.lastShade) {
            gl.glShadeModel(toApply);
            record.lastShade = toApply;
        }

        if (!record.isValid())
            record.validate();
    }

    private int getGLShade() {
        switch (shadeMode) {
            case Flat:
                return GL.GL_FLAT;
            case Smooth:
                return GL.GL_SMOOTH;
        }
        throw new IllegalStateException("unknown shade mode: "+shadeMode);
    }

    @Override
    public ShadeStateRecord createStateRecord() {
        return new ShadeStateRecord();
    }
}
