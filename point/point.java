package point;

public class point implements Comparable<point> {
	private float x, y, z, normal_x, normal_y, normal_z;
	private int color = -1;
	private int[] rgb = null;
	private float[] properties = null;
	private float curvature = -1;
	private DataType type;

	public point(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = DataType.XYZ;
	}

	public point(float x, float y, float z, int color) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;
		this.type = DataType.XYZRGB;
	}

	public point(float x, float y, float z, float normal_x, float normal_y,
			float normal_z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.normal_x = normal_x;
		this.normal_y = normal_y;
		this.normal_z = normal_z;
		this.type = DataType.XYZNORMAL;
	}

	public point(float x, float y, float z, float curvature) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.curvature = curvature;
		this.type = DataType.XYZC;
	}

	public point(float x, float y, float z, float curvature, float normal_x,
			float normal_y, float normal_z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.curvature = curvature;
		this.normal_x = normal_x;
		this.normal_y = normal_y;
		this.normal_z = normal_z;
		this.type = DataType.XYZCNORMAL;
	}

	public DataType getType() {
		return this.type;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

	public float getCurvature() {
		return this.curvature;
	}

	public int getRGB() {
		return this.color;
	}

	public int[] parseRGB() {
		if (rgb == null && color != -1) {
			rgb = new int[3];
			rgb[0] = (color >> 16) & 0x0000ff;
			rgb[1] = (color >> 8) & 0x0000ff;
			rgb[2] = (color) & 0x0000ff;
		}

		return rgb;
	}

	public float[] getProperties() {
		switch (type) {
		case XYZ:
			return getXYZProperties();
		case XYZRGB:
			return getXYZRGBProperties();
		case XYZNORMAL:
			return getXYZNORMALProperties();
		case XYZC:
			return getXYZCProperties();
		case XYZCNORMAL:
			return getXYZCNORMALProperties();
		default:
			return null;
		}
	}

	private float[] getXYZCProperties() {
		if (properties == null)
			properties = new float[] { this.x, this.y, this.z, this.curvature };
		return properties;
	}

	private float[] getXYZCNORMALProperties() {
		if (properties == null)
			properties = new float[] { this.x, this.y, this.z, this.curvature,
					this.color };
		return properties;
	}

	private float[] getXYZProperties() {
		if (properties == null)
			properties = new float[] { this.x, this.y, this.z };
		return properties;
	}

	private float[] getXYZRGBProperties() {
		if (properties == null)
			properties = new float[] { this.x, this.y, this.z, this.color };
		return properties;
	}

	private float[] getXYZNORMALProperties() {
		if (properties == null)
			properties = new float[] { this.x, this.y, this.z, this.normal_x,
					this.normal_y, this.normal_z };
		return properties;
	}

	@Override
	public int compareTo(point other) {
		if (other == null)
			return 1;
		else if (this.x > other.getX())
			return 1;
		else if (this.x < other.getX())
			return -1;
		else if (this.y > other.getY())
			return 1;
		else if (this.y < other.getY())
			return -1;
		else if (this.z > other.getZ())
			return 1;
		else if (this.z < other.getZ())
			return -1;
		else
			return 0;
	}

	public float disTo(point other) {
		if (other == null)
			return 0;
		else
			return (float) Math.sqrt((this.x - other.getX())
					* (this.x - other.getX()) + (this.y - other.getY())
					* (this.y - other.getY()) + (this.z - other.getZ())
					* (this.z - other.getZ()));
	}
}
