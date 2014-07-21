/*
*   GLUtils -- A set of static utilities for rendering simple shapes in OpenGL.
*   
*   Copyright (C) 2002-2004 by Joseph A. Huwaldt.
*   All rights reserved.
*   
*   This library is free software; you can redistribute it and/or
*   modify it under the terms of the GNU Lesser General Public
*   License as published by the Free Software Foundation; either
*   version 2 of the License, or (at your option) any later version.
*   
*   This library is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*   Lesser General Public License for more details.
*
*   You should have received a copy of the GNU Lesser General Public License
*   along with this program; if not, write to the Free Software
*   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*   Or visit:  http://www.gnu.org/licenses/lgpl.html
**/

package util;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;



/**
*  A set of static utility methods for rendering various simple
*  shapes in OpenGL using JOGL.  In particular, these methods
*  are meant to compliment those in the standard GLU.
*
*  <p>  Modified by:  Joseph A. Huwaldt  </p>
*
*  @author  Joseph A. Huwaldt   Date:  May 13, 2002
*  @version October 16, 2004
**/
public class GLUtils {
	
	
	/**
	*  Prevent anyone from instantiating this utiltity class.
	**/
	private GLUtils() {}
	

    /**
    *  <p> Draws the outline of a 2D circle centered
    *      on the specified point and contained in the XY plane (z=0).
    *      Uses an efficient parametric algorithm to avoid expensive
    *      calls to triginometric functions.  Verticies are evenly
    *      spaced in parameter space.
    *  </p>
    *
    *  <p> Reference:  Rogers, D.F., Adams, J.A.,
    *                  _Mathematical_Elements_For_Computer_Graphics_,
    *                  McGraw-Hill, 1976, pg 103, 216.
    *  </p>
    *
    *  @param  gl   Reference to the graphics context we are rendering into.
    *  @param  x    X Coordinate of the center of the circle.
    *  @param  y    Y Coordinate of the center of the circle.
    *  @param  radius    The radius of the cirlce.
    *  @param  numVerts  The number of verticies to use.
    **/
    public static final void drawCircle(GL2 gl, float x, float y, float radius, int numVerts) {

        //  Calculate the parametric increment.
        double p = 2*Math.PI/(numVerts-1);
        float c1 = (float)Math.cos(p);
        float s1 = (float)Math.sin(p);

        //  Calculate the initial point.
        float xm1 = x + radius;
        float ym1 = y;

        //  Begin rendering the circle.
        gl.glBegin(GL.GL_LINE_LOOP);
        for (int m=0; m < numVerts; ++m) {
            float xm1mx = xm1 - x;
            float ym1mx = ym1 - y;
            float x1 = x + xm1mx*c1 - ym1mx*s1;
            float y1 = y + xm1mx*s1 + ym1mx*c1;

            //  Draw the next line segment.
            gl.glVertex3f(x1, y1, 0);

            //  Prepare for the next loop.
            xm1 = x1;
            ym1 = y1;
        }
        gl.glEnd();
        
    }

    
    /**
    *  <p> Draws the outline of a 2D inclined ellipse centered
    *      on the specified point and contained in the XY plane (z=0).
    *      Uses an efficient parametric algorithm to avoid expensive
    *      calls to triginometric functions.  Verticies are evenly spaced
    *      in parameter space.
    *      If "a" and "b" are equal, a circle will be rendered,
    *      however the function "circle" is a bit more efficient
    *      for that special case.
    *  </p>
    *
    *  <p> Reference:  Rogers, D.F., Adams, J.A.,
    *                  _Mathematical_Elements_For_Computer_Graphics_,
    *                  McGraw-Hill, 1976, pg 104, 216.
    *  </p>
    *
    *  @param  gl   Reference to the graphics context we are rendering into.
    *  @param  x    X Coordinate of the center of the ellipse.
    *  @param  y    Y Coordinate of the center of the ellipse.
    *  @param  a    Length of the semi-major axis of the ellipse, oriented
    *               along the X axis.
    *  @param  b    Length of the semi-minor axis of the ellipse, oriented
    *               along the Y axis.
    *  @param  inc  Inclination angle of the major axis in degrees.
    *  @param  numVerts  The number of verticies to use.
    **/
    public static final void drawEllipse(GL2 gl, float x, float y, float a, float b,
                                    float inc, int numVerts) {

        //  Convert inclination to radians.
        double p = inc*Math.PI/180;

        //  Calculate the sine and cosine of the inclination angle.
        float c1 = (float)Math.cos(p);
        float s1 = (float)Math.sin(p);

        //  Calculate the parametric increment.
        p = 2*Math.PI/(numVerts-1);

        //  Calculate the sine and cosine of the parametric increment.
        float c2 = (float)Math.cos(p);
        float s2 = (float)Math.sin(p);
        
        //  Initialize the accumulation variables.
        float c3 = 1;
        float s3 = 0;

        //  Begin rendering the ellipse.
        gl.glBegin(GL.GL_LINE_LOOP);
        for (int m=0; m < numVerts; ++m) {
            //  Calculate increments in X & Y.
            float x1 = a*c3;
            float y1 = b*s3;

            //  Calculate new X & Y.
            float xm = x + x1*c1 - y1*s1;
            float ym = y + x1*s1 + y1*c1;

            //  Draw the next line segment.
            gl.glVertex3f(xm, ym, 0);

            //  Calculate new angle values.
            float t1 = c3*c2 - s3*s2;
            s3 = s3*c2 + c3*s2;
            c3 = t1;
        }
        gl.glEnd();
        
    }

    
    /**
    *  <p> Draws the outline of a 2D inclinded elliptical arc starting at "angle"
    *      and extending to angle + sweep, centered on the specified point,
    *      and contained in the XY plane (z=0).  Note that "angle" is defined
    *      differently than it is for gluPartialDisk()!
    *      Uses an efficient parametric algorithm to avoid expensive
    *      calls to triginometric functions.  Verticies are evenly spaced
    *      in parameter space. If "a" and "b" are equal, a circular
    *      arc will be rendered.
    *  </p>
    *
    *  <p> Reference:  Rogers, D.F., Adams, J.A.,
    *                  _Mathematical_Elements_For_Computer_Graphics_,
    *                  McGraw-Hill, 1976, pg 104, 216.
    *  </p>
    *
    *  @param  gl     Reference to the graphics context we are rendering into.
    *  @param  x      X Coordinate of the center of the ellipse defining the arc.
    *  @param  y      Y Coordinate of the center of the ellipse defining the arc.
    *  @param  a      Length of the semi-major axis of the ellipse
    *                 defining the arc, oriented along the X axis.
    *  @param  b      Length of the semi-minor axis of the ellipse
    *                 defining the arc, oriented along the Y axis.
    *  @param  inc    Inclination angle of the major axis in degrees.
    *  @param  angle  The initial angle to begin the arc in degrees.  0 is
    *                 along the + major axis (+X for zero inc), 90 is
    *                 along the + minor axis (+Y for zero inc), 180 along
    *                 the - major axis and 270 degrees is along the
    *                 - minor axis.
    *  @param  sweep  The number of degrees to sweep the arc through.
    *  @param  numVerts  The number of verticies to use.
    **/
    public static final void drawArc(GL2 gl, float x, float y, float a, float b,
                                    float inc, float angle, float sweep, int numVerts) {

        //  Calculate the sine and cosine of the inclination angle.
        double p = inc*Math.PI/180;
        float c1 = (float)Math.cos(p);
        float s1 = (float)Math.sin(p);

        //  Calculate the parametric increment.
        p = sweep*Math.PI/180/(numVerts-1);

        //  Calculate the sine and cosine of the parametric increment.
        float c2 = (float)Math.cos(p);
        float s2 = (float)Math.sin(p);
        
        //  Initialize the accumulation variables.
        p = angle*Math.PI/180;
        float c3 = (float)Math.cos(p);
        float s3 = (float)Math.sin(p);

        //  Begin rendering the arc.
        gl.glBegin(GL.GL_LINE_STRIP);
        for (int m=0; m < numVerts; ++m) {
            //  Calculate increments in X & Y.
            float x1 = a*c3;
            float y1 = b*s3;

            //  Calculate new X & Y.
            float xm = x + x1*c1 - y1*s1;
            float ym = y + x1*s1 + y1*c1;

            //  Draw the next line segment.
            gl.glVertex3f(xm, ym, 0);

            //  Calculate new angle values.
            float t1 = c3*c2 - s3*s2;
            s3 = s3*c2 + c3*s2;
            c3 = t1;
        }
        gl.glEnd();
        
    }

}
