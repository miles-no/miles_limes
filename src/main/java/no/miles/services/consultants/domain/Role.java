package no.miles.services.consultants.domain;

import java.util.EnumSet;

public enum Role {
    ARCHITECTURE("6425649e5d697e0f43838a16"),
    DATA_SCIENCE("642565189b513710125e3deb"),
    DESIGN("642564b29b513710125e3dd4"),
    PROJECT_MANAGEMENT("642564c1d658eb0f9fd23aa0"),
    ADVISORY("642565049b51370fe35e3c82"),
    AGILE("642564e35604610f9f3b9ed9"),
    TEST_MANAGEMENT("642564d15604610f9d3b9dbc"),
    DEVELOPMENT("6425646e5604610f973b9d9d"),
    HIDDEN_CONSULTANT("6489d2c7c2c2ad0f28dc9eab");

    public static final EnumSet<Role> values = EnumSet.allOf(Role.class);
    private final String roleId;

    Role(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleId() {
        return roleId;
    }
}
