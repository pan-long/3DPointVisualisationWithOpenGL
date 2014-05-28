package quaternion;

public class Quaternion {
    private static float TRACKBALLSIZE;
    private static Quaternion quaternionIns = null;

    private Quaternion() {
    }

    public static Quaternion getIns(float ballSize) {
        if (quaternionIns == null)
            quaternionIns = new Quaternion();
        TRACKBALLSIZE = ballSize;

        return quaternionIns;
    }

    private float[] getVector(float x, float y, float z) {
        return new float[] { x, y, z };
    }

    private float[] VectorSub(float[] src1, float[] src2) {
        return new float[] { src1[0] - src2[0], src1[1] - src2[1],
                src1[2] - src2[2] };
    }

    private float[] VectorCross(float[] v1, float[] v2) {
        return new float[] { v1[1] * v2[2] - v1[2] * v2[1],
                v1[2] * v2[0] - v1[0] * v2[2], v1[0] * v2[1] - v1[1] * v2[0] };
    }

    private float getLength(float[] v) {
        return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }

    private float[] scaleVector(float[] v, float div) {
        return new float[] { v[0] * div, v[1] * div, v[2] * div };
    }

    private float[] getVectorNormal(float[] v) {
        return scaleVector(v, 1 / getLength(v));
    }

    private float VectorDot(float[] v1, float[] v2) {
        return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
    }

    private float[] VectorAdd(float[] v1, float[] v2) {
        return new float[] { v1[0] + v2[0], v1[1] + v2[1], v1[2] + v2[2] };
    }

    public float[] TrackBall(float p1x, float p1y, float p2x, float p2y) {
        float[] a = null;
        float phi;
        float[] p1 = null;
        float[] p2 = null;
        float[] d = null;
        float t;

        if (p1x == p2x && p1y == p2y) {
            return new float[] { 0, 0, 0, 1 };
        }

        p1 = getVector(p1x, p1y, ProjectToSphere(TRACKBALLSIZE, p1x, p1y));
        p2 = getVector(p2x, p2y, ProjectToSphere(TRACKBALLSIZE, p2x, p2y));

        a = VectorCross(p2, p1);
        d = VectorSub(p1, p2);

        t = getLength(d) / (2 * TRACKBALLSIZE);

        if (t > 1.0)
            t = 1.0f;
        if (t < -1.0)
            t = -1.0f;
        phi = (float) (2.0 * Math.asin(t));

        return AxisToQuat(a, phi);
    }

    private float[] AxisToQuat(float[] a, float phi) {
        float[] q = new float[4];
        float[] a_norm = getVectorNormal(a);
        for (int i = 0; i < 3; i++) {
            q[i] = a_norm[i] * (float)Math.sin(phi / 2);
        }

        /* q = scaleVector(q, (float) Math.sin(phi / 2)); */
        q[3] = (float) Math.cos(phi / 2);
        return q;
    }

    private float ProjectToSphere(float r, float x, float y) {
        float d, t, z;

        d = (float) Math.sqrt(x * x + y * y);
        if (d < r * 0.70710678118654752440f) {
            z = (float) Math.sqrt(r * r - d * d);
        } else {
            t = r / 1.41421356237309504880f;
            z = t * t / d;
        }

        return z;
    }

    private float[] normalizeQuat(float[] q) {
        int i;
        float mag;

        mag = (q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
        for (i = 0; i < 4; i++) {
            q[i] /= mag;
        }

        return q;
    }

    public float[][] buildRotMatrix(float[] q) {
        float[][] m = new float[4][4];

        m[0][0] = 1.0f - 2.0f * (q[1] * q[1] + q[2] * q[2]);
        m[0][1] = 2.0f * (q[0] * q[1] - q[2] * q[3]);
        m[0][2] = 2.0f * (q[2] * q[0] + q[1] * q[3]);
        m[0][3] = 0.0f;

        m[1][0] = 2.0f * (q[0] * q[1] + q[2] * q[3]);
        m[1][1] = 1.0f - 2.0f * (q[2] * q[2] + q[0] * q[0]);
        m[1][2] = 2.0f * (q[1] * q[2] - q[0] * q[3]);
        m[1][3] = 0.0f;

        m[2][0] = 2.0f * (q[2] * q[0] - q[1] * q[3]);
        m[2][1] = 2.0f * (q[1] * q[2] + q[0] * q[3]);
        m[2][2] = 1.0f - 2.0f * (q[1] * q[1] + q[0] * q[0]);
        m[2][3] = 0.0f;

        m[3][0] = 0.0f;
        m[3][1] = 0.0f;
        m[3][2] = 0.0f;
        m[3][3] = 1.0f;

        return m;
    }
}
