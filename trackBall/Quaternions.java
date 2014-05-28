package trackBall;

import java.util.ArrayList;

public final class Vectors
{
    public static float[] add (float[] v1, float[] v2, float[] dst)
    {
        if (dst == null) dst = new float[3];
        
        for (int i = 0; i < 3; i++) dst[i] = v1[i] + v2[i];
        return dst;
    }
    public static float[] add (float[] v, float s, float[] dst)
    {
        if (dst == null) dst = new float[3];
        
        for (int i = 0; i < 3; i++) dst[i] = v[i] + s;
        return dst;
    }
    
    public static float[] add_r (float[] v1, float[] v2)
    {
        return add(v1, v2, v1);
    }
    
    public static float[] add_r (float[] v, float s)
    {
        return add(v, s, v);
    }
    
    public static float[] sub (float[] v1, float[] v2, float[] dst)
    {
        if (dst == null) dst = new float[3];
        
        for (int i = 0; i < 3; i++) dst[i] = v1[i] - v2[i];
        return dst;
    }
    
    public static float[] sub (float[] v, float s, float[] dst)
    {
        if (dst == null) dst = new float[3];
        
        for (int i = 0; i < 3; i++) dst[i] = v[i] - s;
        return dst;
    }
    
    public static float[] sub_r (float[] v1, float[] v2)
    {
        return sub(v1, v2, v1);
    }
    
    public static float[] sub_r (float[] v, float s)
    {
        return sub(v, s, v);
    }
    
    public static float[] mul (float[] v, float s, float[] dst)
    {
        if (dst == null) dst = new float[3];
        
        for (int i = 0; i < 3; i++) dst[i] = v[i] * s;
        return dst;
    }
    
    public static float[] mul_r (float[] v, float s)
    {
        return mul(v, s, v);
    }
    
    public static float[] div (float[] v, float s, float[] dst)
    {
        for (int i = 0; i < 3; i++) dst[i] = v[i] * s;
        return dst;     
    }
    
    public static float[] div_r (float[] v, float s)
    {
        return div(v, s, v);
    }
    
    public static float[] cross (float[] v1, float[] v2, float[] dst)
    {
        float tx = (v1[1] * v2[2]) - (v1[2] * v2[1]);
        float ty = (v1[2] * v2[0]) - (v1[0] * v2[2]);
        float tz = (v1[0] * v2[1]) - (v1[1] * v2[0]);
        
        if (dst != null) { dst[0] = tx; dst[1] = ty; dst[2] = tz; }
        else dst = new float[] {tx, ty, tz};
        
        return dst;
    }
    
    public static float[] cross_r (float[] v1, float[] v2)
    {
        return cross(v1, v2, v1);
    }
    
    public static float[] inverse (float[] v, float[] dst)
    {
        if (dst == null) dst = new float[3];

        for (int i = 0; i < 3; i++) dst[i] = -v[i];
        return dst;
    }
    
    public static float[] inverse_r (float[] v)
    {
        return inverse(v, v);
    }
    
    public static float[] norm (float[] v, float[] dst)
    {
        if (dst == null) dst = new float[3];
        
        float l = Vectors.len(v);

        for (int i = 0; i < 3; i++) dst[i] = v[i] / l;
        return dst;
    }
    
    public static float[] norm_r (float[] v)
    {
        return norm(v, v);
    }
    
    public static float dot (float[] v1, float[] v2)
    {
        float dot = 0;
        
        for (int i = 0; i < 3; i++) dot += v1[i] * v2[i];
        return dot;
    }
    
    public static float len (float[] v)
    {
        return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }
    
    public static String toString (float[] v)
    {
        StringBuilder s = new StringBuilder();
        s.append("[ X: ");
        s.append(v[0]);
        s.append(" Y: ");
        s.append(v[1]);
        s.append(" Z: ");
        s.append(v[2]);
        s.append(" ]");
        
        return s.toString();
    }
}
