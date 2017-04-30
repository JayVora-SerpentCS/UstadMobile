package com.ustadmobile.ustadmobile.ormlite.generated.model;

import com.j256.ormlite.table.DatabaseTable;
import com.ustadmobile.port.sharedse.persistence.proxy.MgmtTest;
import com.j256.ormlite.field.DatabaseField;

@DatabaseTable(tableName = "mgmt_test")
public class MgmtTestEntity implements MgmtTest {

	static public final String COLNAME_TEST_ID = "test_id";
	@DatabaseField(columnName = COLNAME_TEST_ID, id = true)
	private String testId;
	static public final String COLNAME_CANONICAL_DATA = "canonical_data";
	@DatabaseField(columnName = COLNAME_CANONICAL_DATA)
	private String canonicalData;

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getCanonicalData() {
		return canonicalData;
	}

	public void setCanonicalData(String canonicalData) {
		this.canonicalData = canonicalData;
	}
}