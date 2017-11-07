package util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

public interface Inf_KHXMLUtil {
	/**
	 * 解析XML文档，获取Document对象
	 * 
	 * @param url
	 *            XML文件的路径，需要new File().toURL()传入
	 * @return 解析XML文档，返回解析后的Document对象
	 * @throws DocumentException
	 *             文档找不到
	 */
	public Document parse(URL url) throws DocumentException;

	/**
	 * 从传入的XML文件对象，获取确定的function节点的所有属性，并将找到的function节点存到eleFunction上，可通过
	 * getEleFunction()获取到找到的function节点
	 * 
	 * @param document
	 *            传入已经解析好的XML对象
	 * @param functionName
	 *            需要搜寻的function节点
	 * @return 返回function节点的所有属性，还有一个操作即将找到的function节点存到eleFunction上，
	 *         可通过getEleFunction()获取
	 * @throws Exception
	 *             搜寻所有的function节点都没找到目标节点，则抛出找不到function节点的异常
	 */
	public Map<String, String> getFunctionElementConfigs(Document document, String functionName) throws Exception;

	/**
	 * 通过确切的function节点，查找function节点下属table节点，并将找到的table节点存到eleTable上，可通过
	 * getEleTable()()获取到找到的function节点
	 * 
	 * @param function
	 *            传入已经搜寻到的function节点
	 * @param tableName
	 *            需要搜寻的table节点
	 * @return 返回table节点的所有属性，还有一个操作即将找到的table节点存到eleTable上， 可通过getEleTable()获取
	 * @throws Exception
	 *             搜寻所有的table节点都没找到目标节点，则抛出找不到table节点的异常
	 */
	public Map<String, Map<String, String>> getTableElementConfigs(Element function, String tableName) throws Exception;

	/**
	 * 搜寻传入element下的所有节点并遍历所有的属性，默认是搜索所有的FiledConfig节点，但也可以搜索function、table节点等。
	 * 
	 * @param element
	 *            需要搜索的节点
	 * @throws DocumentException
	 */
	public void searchFieldConfigs(Element element) throws DocumentException;

	/**
	 * 获取所有检索到的Table节点
	 * 
	 * @return
	 */
	public ArrayList<Element> getEleTable();

	/**
	 * 获取Function节点的属性，根据Function的Name存储key，属性存储到value中
	 * 
	 * @return
	 */
	public Map<String, String> getFunctionConfigs();

	/**
	 * 获取Table节点的属性，根据Table节点Name存储key，属性存到Map中
	 * 
	 * @return
	 */
	public Map<String, Map<String, String>> getTableConfigs();

	/**
	 * 获取Field节点的属性，每一个Field的Name作为检索的key，属性存放在Map中
	 * 
	 * @return
	 */
	public Map<String, Map<String, String>> getFieldConfigs();

	/**
	 * <ul>
	 * 根据提供的XML文件路径，提供的functionName自动获取function、table和field的属性
	 * <li>调用此方法后，<strong >需要调用
	 * <span style="color:red;">getFunctionConfigs()</span>
	 * 获取function节点的所有属性</strong></li>
	 * <li><strong >需要调用<span style="color:red;">getTableConfigs()</span>
	 * 获取所有table节点的属性；</strong></li>
	 * <li><strong>需要调用<span style="color:red;">getFieldConfigs()</span>
	 * 获取所有field节点的属性</strong>，field <strong>上一层table节点存储在第一位</strong>。
	 * 如果field都有上一层table节点，都会放在返回值的第一位</li>
	 * </ul>
	 * 
	 * @param url
	 *            XML文件的路径
	 * @param functionName
	 *            提供需要查询的function名字
	 * @return 搜寻到的所有节点的属性都保留在返回值中
	 * @throws Exception
	 */
	public void autoLoadXMLFunctionNode(URL url, String functionName) throws Exception;
}
