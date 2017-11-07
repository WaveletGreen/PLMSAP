package util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

import com.sap.conn.jco.ext.DestinationDataProvider;

import util.KHXMLUtil;

/**
 * 连接池则是在需要时才建立连接，连接暂不需要时，将被释放回连接池，再分配给其他用户使用。 直接写配置文件在内存中
 * 在该对象中，有一个默认的Properties对象，提供默认的连接SAP方式（测试方式）getDefaultProperties()，
 * <strong style="color:red;">如果正式上线可能需要调用changeProperties( String fileName,
 * String OrganizeName)方法更改连接方式</strong>
 *
 *
 * 调用destination属性时就会发起连接，一直等待远程响应
 */
public class ConnectPooled {

	private static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";// 这个字符串定义了是连接池方式连接还是直接连接：ABAP_AS_WITHOUT_POOL是直连，ABAP_AS_WITH_POOL是连接池连接方式
	private static Properties CONNECT_POOOL;
	static {
		// 不配置文件
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
	 * 获取默认的连接SAP系统的方式，该方式直接的描述放在util包下面的SAPIntegrationConfig4.XML文档Organize节点
	 * 
	 * @return 返回默认的Properties连接方式
	 */
	public static Properties getDefaultProperties() {
		return CONNECT_POOOL;
	}

	/**
	 * 更换连接SAP的方式，通过解析传入的XML文件获取连接SAP的IP地址、主机名、用户名和密码等信息
	 * 此次更改Properties之后，则此次的更改Properties设为默认的Properties
	 * 
	 * @param fileName
	 *            确切的XML文件路径，该文件必须存在，否则会发生异常
	 * @param OrganizeName
	 *            </br>
	 *            <table>
	 *            OrganizeName节点必须包含以下属性
	 *            <tr>
	 *            <td>AppServerHost</td>
	 *            <td><strong>服务器地址</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>SystemNumber</td>
	 *            <td><strong>系统编号</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>Client</td>
	 *            <td><strong>Client号</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>User</td>
	 *            <td><strong>连接SAP的用户名</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>Password</td>
	 *            <td><strong>连接SAP的用户密码</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>Language</td>
	 *            <td><strong>语言</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>JCO_PEAK_LIMIT</td>
	 *            <td><strong>最大连接数</strong></td>
	 *            </tr>
	 *            <tr>
	 *            <td>PoolSize</td>
	 *            <td><strong>连接池大小</strong></td>
	 *            </tr>
	 *            </table>
	 * @return <strong>返回的是Properties对象，如果执行解析XML文件格式正确，则返回正确的数据，
	 *         反正可能会返回错误的数据甚至报异常</strong>
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
		// 清空
		CONNECT_POOOL.clear();
		// 再填入屬性
		putInfoIntoProperties(CONNECT_POOOL, util);
		return CONNECT_POOOL;
	}

	/**
	 * 根据约定，将传入的对象CONNECT_POOOL填充属性内容
	 * 
	 * @param CONNECT_POOOL
	 *            需要填充确定属性的Properties对象
	 * @return
	 */
	private static Properties putInfoIntoProperties(Properties CONNECT_POOOL, KHXMLUtil util) {
		Map<String, String> Organize = util.getFunctionConfigs();
		// 配置服务器地址
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_ASHOST, Organize.get("AppServerHost"));
		// 配置系统编号
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_SYSNR, Organize.get("SystemNumber"));
		// 配置客户端
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_CLIENT, Organize.get("Client"));
		// 配置用户名
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_USER, Organize.get("User"));
		// 注：密码是区分大小写的，要注意大小写
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_PASSWD, Organize.get("Password"));
		// 配置语言
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_LANG, Organize.get("Language"));
		// *********连接池方式与直接不同的是设置了下面两个连接属性
		// JCO_PEAK_LIMIT - 同时可创建的最大活动连接数，0表示无限制，默认为JCO_POOL_CAPACITY的值
		// 如果小于JCO_POOL_CAPACITY的值，则自动设置为该值，在没有设置JCO_POOL_CAPACITY的情况下为0
		CONNECT_POOOL.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, Organize.get("JCO_PEAK_LIMIT"));
		// JCO_POOL_CAPACITY - 空闲连接数，如果为0，则没有连接池效果，默认为1
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
