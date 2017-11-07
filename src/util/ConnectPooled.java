package util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

import com.sap.conn.jco.ext.DestinationDataProvider;

import util.KHXMLUtil;

/**
 * ���ӳ���������Ҫʱ�Ž������ӣ������ݲ���Ҫʱ�������ͷŻ����ӳأ��ٷ���������û�ʹ�á� ֱ��д�����ļ����ڴ���
 * �ڸö����У���һ��Ĭ�ϵ�Properties�����ṩĬ�ϵ�����SAP��ʽ�����Է�ʽ��getDefaultProperties()��
 * <strong style="color:red;">�����ʽ���߿�����Ҫ����changeProperties( String fileName,
 * String OrganizeName)�����������ӷ�ʽ</strong>
 *
 *
 * ����destination����ʱ�ͻᷢ�����ӣ�һֱ�ȴ�Զ����Ӧ
 */
public class ConnectPooled {

	private static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";// ����ַ��������������ӳط�ʽ���ӻ���ֱ�����ӣ�ABAP_AS_WITHOUT_POOL��ֱ����ABAP_AS_WITH_POOL�����ӳ����ӷ�ʽ
	private static Properties CONNECT_POOOL;
	static {
		// �������ļ�
		CONNECT_POOOL = new Properties();
		KHXMLUtil util = new KHXMLUtil();
		try {
			util.parse(new File("src/util/SAPIntegrationConfig4.xml").toURL());
			util.getFunctionElementConfigs(util.getDocument(), "KHYJ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CONNECT_POOOL.clear();
		putInfoIntoProperties(CONNECT_POOOL, util);
	}

	/**
	 * ��ȡĬ�ϵ�����SAPϵͳ�ķ�ʽ���÷�ʽֱ�ӵ���������util�������SAPIntegrationConfig4.XML�ĵ�Organize�ڵ�
	 * 
	 * @return ����Ĭ�ϵ�Properties���ӷ�ʽ
	 */
	public static Properties getDefaultProperties() {
		return CONNECT_POOOL;
	}

	/**
	 * ��������SAP�ķ�ʽ��ͨ�����������XML�ļ���ȡ����SAP��IP��ַ�����������û������������Ϣ
	 * �˴θ���Properties֮����˴εĸ���Properties��ΪĬ�ϵ�Properties
	 * 
	 * @param fileName
	 *            ȷ�е�XML�ļ�·�������ļ�������ڣ�����ᷢ���쳣
	 * @param OrganizeName
	 *            </br>
	 *            <table>
	 *            OrganizeName�ڵ���������������
	 *            <tr>
	 *            <td>AppServerHost</td>
	 *            <td><strong>��������ַ</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>SystemNumber</td>
	 *            <td><strong>ϵͳ���</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>Client</td>
	 *            <td><strong>Client��</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>User</td>
	 *            <td><strong>����SAP���û���</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>Password</td>
	 *            <td><strong>����SAP���û�����</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>Language</td>
	 *            <td><strong>����</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>JCO_PEAK_LIMIT</td>
	 *            <td><strong>���������</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>PoolSize</td>
	 *            <td><strong>���ӳش�С</strong></td>
	 *            </tr>
	 *            </table>
	 * @return <strong>���ص���Properties�������ִ�н���XML�ļ���ʽ��ȷ���򷵻���ȷ�����ݣ�
	 *         �������ܻ᷵�ش���������������쳣</strong>
	 */
	public static Properties changeProperties(String fileName, String OrganizeName) {
		KHXMLUtil util = new KHXMLUtil();
		try {
			util.parse(new File(fileName).toURL());
			util.getFunctionElementConfigs(util.getDocument(), OrganizeName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ���
		CONNECT_POOOL.clear();
		// ���������
		putInfoIntoProperties(CONNECT_POOOL, util);
		return CONNECT_POOOL;
	}

	/**
	 * ����Լ����������Ķ���CONNECT_POOOL�����������
	 * 
	 * @param CONNECT_POOOL
	 *            ��Ҫ���ȷ�����Ե�Properties����
	 * @return
	 */
	private static Properties putInfoIntoProperties(Properties CONNECT_POOOL, KHXMLUtil util) {
		Map<String, String> Organize = util.getFunctionConfigs();
		// ���÷�������ַ
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_ASHOST, Organize.get("AppServerHost"));
		// ����ϵͳ���
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_SYSNR, Organize.get("SystemNumber"));
		// ���ÿͻ���
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_CLIENT, Organize.get("Client"));
		// �����û���
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_USER, Organize.get("User"));
		// ע�����������ִ�Сд�ģ�Ҫע���Сд
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_PASSWD, Organize.get("Password"));
		// ��������
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_LANG, Organize.get("Language"));
		// *********���ӳط�ʽ��ֱ�Ӳ�ͬ��������������������������
		// JCO_PEAK_LIMIT - ͬʱ�ɴ����������������0��ʾ�����ƣ�Ĭ��ΪJCO_POOL_CAPACITY��ֵ
		// ���С��JCO_POOL_CAPACITY��ֵ�����Զ�����Ϊ��ֵ����û������JCO_POOL_CAPACITY�������Ϊ0
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, Organize.get("JCO_PEAK_LIMIT"));
		// JCO_POOL_CAPACITY - ���������������Ϊ0����û�����ӳ�Ч����Ĭ��Ϊ1
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, Organize.get("PoolSize"));
		createDataFile(ABAP_AS_POOLED, "jcoDestination", CONNECT_POOOL);
		return CONNECT_POOOL;
	}

	private static void createDataFile(String name, String suffix, Properties properties) {
		File cfg = new File(name + "." + suffix);
		if (!cfg.exists()) {
			try {
				FileOutputStream fos = new FileOutputStream(cfg, false);
				properties.store(fos, "Store Properties");
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
