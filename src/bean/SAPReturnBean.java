package bean;

public class SAPReturnBean {
	public static String[] SAPReturnBeanParams = { "TYPE", "ID", "NUMBER", "MESSAGE", "LOG_NOv", "LOG_MSG_NO", "MESSAGE_V1",
			"MESSAGE_V2", "MESSAGE_V3", "MESSAGE_V4", "PARAMETER", "ROW", "FIELD", "SYSTEM" };
	public static String RetTableName = "RETURN";// 返回的table名，根据这个名称回去JcoTable
	private String TYPE;// 消息类型: S 成功,E 错误,W 警告,I 信息,A 中断
	private String ID;// 消息, 消息类
	private String NUMBER;// 消息, 消息编号
	private String MESSAGE;// 消息文本
	private String LOG_NOv;// 忽略
	private String LOG_MSG_NO;// 忽略
	private String MESSAGE_V1;// 忽略
	private String MESSAGE_V2;// 忽略
	private String MESSAGE_V3;// 忽略
	private String MESSAGE_V4;// 忽略
	private String PARAMETER;// 忽略
	private String ROW;// 忽略
	private String FIELD;// 忽略
	private String SYSTEM;// 忽略

	public SAPReturnBean() {
		super();
	}

	public SAPReturnBean(String tYPE, String iD, String nUMBER, String mESSAGE, String lOG_NOv, String lOG_MSG_NO,
			String mESSAGE_V1, String mESSAGE_V2, String mESSAGE_V3, String mESSAGE_V4, String pARAMETER, String rOW,
			String fIELD, String sYSTEM) {
		super();
		TYPE = tYPE;
		ID = iD;
		NUMBER = nUMBER;
		MESSAGE = mESSAGE;
		LOG_NOv = lOG_NOv;
		LOG_MSG_NO = lOG_MSG_NO;
		MESSAGE_V1 = mESSAGE_V1;
		MESSAGE_V2 = mESSAGE_V2;
		MESSAGE_V3 = mESSAGE_V3;
		MESSAGE_V4 = mESSAGE_V4;
		PARAMETER = pARAMETER;
		ROW = rOW;
		FIELD = fIELD;
		SYSTEM = sYSTEM;
	}

	public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getNUMBER() {
		return NUMBER;
	}

	public void setNUMBER(String nUMBER) {
		NUMBER = nUMBER;
	}

	public String getMESSAGE() {
		return MESSAGE;
	}

	public void setMESSAGE(String mESSAGE) {
		MESSAGE = mESSAGE;
	}

	public String getLOG_NOv() {
		return LOG_NOv;
	}

	public void setLOG_NOv(String lOG_NOv) {
		LOG_NOv = lOG_NOv;
	}

	public String getLOG_MSG_NO() {
		return LOG_MSG_NO;
	}

	public void setLOG_MSG_NO(String lOG_MSG_NO) {
		LOG_MSG_NO = lOG_MSG_NO;
	}

	public String getMESSAGE_V1() {
		return MESSAGE_V1;
	}

	public void setMESSAGE_V1(String mESSAGE_V1) {
		MESSAGE_V1 = mESSAGE_V1;
	}

	public String getMESSAGE_V2() {
		return MESSAGE_V2;
	}

	public void setMESSAGE_V2(String mESSAGE_V2) {
		MESSAGE_V2 = mESSAGE_V2;
	}

	public String getMESSAGE_V3() {
		return MESSAGE_V3;
	}

	public void setMESSAGE_V3(String mESSAGE_V3) {
		MESSAGE_V3 = mESSAGE_V3;
	}

	public String getMESSAGE_V4() {
		return MESSAGE_V4;
	}

	public void setMESSAGE_V4(String mESSAGE_V4) {
		MESSAGE_V4 = mESSAGE_V4;
	}

	public String getPARAMETER() {
		return PARAMETER;
	}

	public void setPARAMETER(String pARAMETER) {
		PARAMETER = pARAMETER;
	}

	public String getROW() {
		return ROW;
	}

	public void setROW(String rOW) {
		ROW = rOW;
	}

	public String getFIELD() {
		return FIELD;
	}

	public void setFIELD(String fIELD) {
		FIELD = fIELD;
	}

	public String getSYSTEM() {
		return SYSTEM;
	}

	public void setSYSTEM(String sYSTEM) {
		SYSTEM = sYSTEM;
	}

}
