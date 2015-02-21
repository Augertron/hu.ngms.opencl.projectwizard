package hu.ngms.opencl.projectwizard.util;

public enum OpenCLVersion {
    OpenCL10("1.0"), OpenCL11("1.1"), OpenCL12("1.2"), OpenCL20("2.0");
    
    private String versionString;
    public final static String preferencesKey = "OpenCLVersion";

    OpenCLVersion(String version) {
	this.versionString = version;
    }
    
    public String toString() {
	return this.versionString;
    }
    
    public String getStringForCombo() {
	StringBuilder sb = new StringBuilder("v");
	sb.append(this.versionString);
	return sb.toString();
    }
}
