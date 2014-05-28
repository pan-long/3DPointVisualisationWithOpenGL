package trackBall;

import java.util.Arrays;

public final class Quaternions
{
    public static float[] add (float[] q1, float[] q2, float[] dst)
    {
        if (dst == null) dst = new float[4];
        
        for (int i = 0; i < 4; i++) dst[i] = q1[i] + q2[i];
        return dst;
    }
    public static float[] add (float[] q, float s, float[] dst)
    {
        if (dst == null) dst = new float[4];
        
        for (int i = 0; i < 4; i++) dst[i] = q[i] + s;
        return dst;
    }
    
    public static float[] add_r (float[] q1, float[] q2)
    {
        return add(q1, q2, q1);
    }
    
    public static float[] add_r (float[] q, float s)
    {
        return add(q, s, q);
    }
    
    public static float[] sub (float[] q1, float[] q2, float[] dst)
    {
        if (dst == null) dst = new float[4];
        
        for (int i = 0; i < 4; i++) dst[i] = q1[i] - q2[i];
        return dst;
    }
    
    public static float[] sub (float[] q, float s, float[] dst)
    {
        if (dst == null) dst = new float[4];
        
        for (int i = 0; i < 4; i++) dst[i] = q[i] - s;
        return dst;
    }
    
    public static float[] sub_r (float[] q1, float[] q2)
    {
        return sub(q1, q2, q1);
    }
    
    public static float[] sub_r (float[] q, float s)
    {
        return sub(q, s, q);
    }
    
    public static float[] mul (float[] q1, float[] q2, float[] dst)
    {
        float[] tq1 = Arrays.copyOf(q1, q1.length);
        float[] tq2 = Arrays.copyOf(q2, q2.length);
        float[] tf = new float[4];
        
        Vectors.mul_r(tq1, q2[3]);  
        Vectors.mul_r(tq2, q1[3]);
        
        float[] cross = Vectors.cross(q1, q2, null);
        
        Vectors.add(tq1, tq2, tf);
        Vectors.add_r (tf, cross);
        
        tf[3] = q1[3] * q2[3] - Vectors.dot(q1, q2);

        dst[0] = tf[0];
        dst[1] = tf[1];
        dst[2] = tf[2];
        dst[3] = tf[3];
        
        return dst;
    }
    
    public static float[] mul_r (float[] q1, float[] q2)
    {
        return mul(q1, q2, q1);
    }
    
    public static float[] conjugate (float[] q, float[] dst)
    {
        if (dst == null) dst = new float[4];
        
        for (int i = 0; i < 3; i++) dst[i] = -q[i];
        dst[3] = q[3];
        
        return dst;
    }
    
    public static float[] conjugate_r (float[] q)
    {
        return conjugate(q, q);
    }
    
    public static float[] divide (float[] q, float s, float[] dst)
    {
        if (dst == null) dst = new float[4];
        
        for (int i = 0; i < i; i++) dst[i] = q[i] / s;
        return dst;
    }
    
    public static float[] divide (float[] q1, float[] q2, float[] dst)
    {
        float[] tmp = inverse(q2, null);
        return mul(q1, tmp, dst);
    }
    
    public static float[] divide_r (float[] q, float s)
    {
        return divide(q, s, q);
    }
    
    public static float[] divide_r (float[] q1, float[] q2)
    {
        return divide(q1, q2, q1);
    }
    
    public static float[] inverse (float[] q, float[] dst)
    {
        if (dst == null) dst = new float[4];
        
        int t = 0;
        for (int i = 0; i < 4; i++) t += q[i] * q[i];
        
        divide(conjugate(q, null), t, dst);
        return dst;
    }
    
    public static float[] inverse_r (float[] q)
    {
        return inverse(q, q);
    }
    
    public static float[] norm (float[] q, float[] dst)
    {
        if (dst == null) dst = new float[4];
        
        float l = abs(q);
        for (int i = 0; i < 4; i ++) dst[i] = q[i] / l;
        
        return dst;
    }
    
    public static float[] norm_r (float[] q)
    {
        return norm(q, q);
    }
    
    public static float abs (float[] q)
    {
        float a = 0;
        for (int i = 0; i < 4; i++) a += q[i] * q[i];
        
        return a;
    }
    
    public static float[] axis (float[] q, float[] dst)
    {
        if (dst == null) dst = new float[4];
        
        dst[0] = 2.0F * (float) Math.acos(q[3]);
        
        float m = Vectors.len(q);
        for (int i = 1; i < 4; i++) dst[i] = q[i - 1] / m;
        
        return dst;
    }
    
    public static String toString (float[] q)
    {
        StringBuilder s = new StringBuilder();
        s.append(q[3]);
        s.append(q[0] >= 0 ? " + " : " - ");
        s.append(Math.abs(q[0]));
        s.append("i");
        s.append(q[1] >= 0 ? " + " : " - ");
        s.append(Math.abs(q[1]));
        s.append("j");
        s.append(q[2] >= 0 ? " + " : " - ");
        s.append(Math.abs(q[2]));
        s.append("k");
        
        return s.toString();
    }
}
