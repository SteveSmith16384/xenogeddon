// Copyright © 2008 jMonkeyEngine, all rights reserved.
// $Id: package-info.java 4075 2008-12-19 13:05:33Z ian.phillips $
/**
 * Standard geometric shapes.
 * <p>
 * While it is possible to create arbitrary shapes using triangle meshes this
 * can be tedious and unnecessary. This package contains a number of standard
 * shapes that can be used that can be directly added to the scene graph, or
 * combined to build up more complex shapes.
 * <p>
 * An important point to note about all of the shapes in this package is that
 * once they have been created they calcuate all of the geometry required for
 * their tri-mesh representation. This has a couple of implications:
 * <p>
 * <ol>
 * <li>the property accessors represent the values that the shape was created with (or the
 *     values from the last update if it has been updated), there is no guarantee that the
 *     actual shape will not have been altered via other means such as calling the methods
 *     on {@link TriMesh} directly; and</li>
 * <li>many of the shapes offer a single methods that sets multiple data values in one hit,
 *     for example {@link Sphere} provides the {@link Sphere#updateGeometry(com.jme.math.Vector3f,
 *     int, int, float)} method. If you are going to update more than one value it will be
 *     considerable more efficient to do so via these methods instead of the individual
 *     property mutators.</li>
 * </ol>
 * 
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 * @version $Revision: 4075 $, $Date: 2008-12-19 06:05:33 -0700 (Fri, 19 Dec 2008) $
 */
package com.jme.scene.shape;