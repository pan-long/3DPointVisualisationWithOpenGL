package trackBall;

public class Trackball
{
    private static final float RADIUS_ = Screen.DFLT_WIDTH / 2.0F;
    private static final int REFRESH_ = 50;
    private static final float SQRT2_ = (float) Math.sqrt(2);
    private static final float SQRT2_INVERSE_ = 1.0F / SQRT2_;

    private int count_;
    private int objx_, objy_;
    private float[] v1_, v2_;
    private float[] rot_;

    public Trackball ()
    {
        v1_ = new float[4];
        v2_ = new float[4];
        rot_ = new float[] {0, 0, 0, 1};
    }

    public void click (int x, int y)
    {
        v1_ = project(x, y);
    }

    public void drag (int x, int y)
    {
        v2_ = project(x, y);

        if (Arrays.equals(v1_, v2_)) return;

        float[] n = Vectors.cross(v2_, v1_, null);
        float[] o = Vectors.sub(v1_, v2_, null);

        float dt = Vectors.len(o) / (2.0F * RADIUS_);

        dt = dt > 1.0F ? 1.0F : dt < -1.0F ? -1.0F : dt;

        float a = 2.0F * (float) Math.asin(dt);

        Vectors.norm_r(n);
        Vectors.mul_r(n, (float) Math.sin(a / 2.0F));

        if (count_++ == REFRESH_) { count_ = 0; Quaternions.norm_r(rot_); }

        float[] q = Arrays.copyOf(n, 4);
        q[3] = (float) Math.cos(a / 2.0F);

        rot_ = Quaternions.mul(q, rot_, rot_);
    }

    public float[] getAxis ()
    {
        return Quaternions.axis(rot_, null);
    }

    public float[] project (float x, float y)
    {
        x = RADIUS_ - objx_ - x;
        y = y - objy_ - RADIUS_;

        float[] v = new float[] {x, y, 0, 0};
        float len = Vectors.len(v);
        float tr = RADIUS_ * SQRT2_INVERSE_;

        if (len < tr)
            v[2] = (float) Math.sqrt(RADIUS_ * RADIUS_ - len * len);
        else
            v[2] = tr * tr / len;

        return v;
    }
}
