package util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

public interface Inf_KHXMLUtil {
	/**
	 * ����XML�ĵ�����ȡDocument����
	 * 
	 * @param url
	 *            XML�ļ���·������Ҫnew File().toURL()����
	 * @return ����XML�ĵ������ؽ������Document����
	 * @throws DocumentException
	 *             �ĵ��Ҳ���
	 */
	public Document parse(URL url) throws DocumentException;

	/**
	 * �Ӵ����XML�ļ����󣬻�ȡȷ����function�ڵ���������ԣ������ҵ���function�ڵ�浽eleFunction�ϣ���ͨ��
	 * getEleFunction()��ȡ���ҵ���function�ڵ�
	 * 
	 * @param document
	 *            �����Ѿ������õ�XML����
	 * @param functionName
	 *            ��Ҫ��Ѱ��function�ڵ�
	 * @return ����function�ڵ���������ԣ�����һ�����������ҵ���function�ڵ�浽eleFunction�ϣ�
	 *         ��ͨ��getEleFunction()��ȡ
	 * @throws Exception
	 *             ��Ѱ���е�function�ڵ㶼û�ҵ�Ŀ��ڵ㣬���׳��Ҳ���function�ڵ���쳣
	 */
	public Map<String, String> getFunctionElementConfigs(Document document, String functionName) throws Exception;

	/**
	 * ͨ��ȷ�е�function�ڵ㣬����function�ڵ�����table�ڵ㣬�����ҵ���table�ڵ�浽eleTable�ϣ���ͨ��
	 * getEleTable()()��ȡ���ҵ���function�ڵ�
	 * 
	 * @param function
	 *            �����Ѿ���Ѱ����function�ڵ�
	 * @param tableName
	 *            ��Ҫ��Ѱ��table�ڵ�
	 * @return ����table�ڵ���������ԣ�����һ�����������ҵ���table�ڵ�浽eleTable�ϣ� ��ͨ��getEleTable()��ȡ
	 * @throws Exception
	 *             ��Ѱ���е�table�ڵ㶼û�ҵ�Ŀ��ڵ㣬���׳��Ҳ���table�ڵ���쳣
	 */
	public Map<String, Map<String, String>> getTableElementConfigs(Element function, String tableName) throws Exception;

	/**
	 * ��Ѱ����element�µ����нڵ㲢�������е����ԣ�Ĭ�����������е�FiledConfig�ڵ㣬��Ҳ��������function��table�ڵ�ȡ�
	 * 
	 * @param element
	 *            ��Ҫ�����Ľڵ�
	 * @throws DocumentException
	 */
	public void searchFieldConfigs(Element element) throws DocumentException;

	/**
	 * ��ȡ���м�������Table�ڵ�
	 * 
	 * @return
	 */
	public ArrayList<Element> getEleTable();

	/**
	 * ��ȡFunction�ڵ�����ԣ�����Function��Name�洢key�����Դ洢��value��
	 * 
	 * @return
	 */
	public Map<String, String> getFunctionConfigs();

	/**
	 * ��ȡTable�ڵ�����ԣ�����Table�ڵ�Name�洢key�����Դ浽Map��
	 * 
	 * @return
	 */
	public Map<String, Map<String, String>> getTableConfigs();

	/**
	 * ��ȡField�ڵ�����ԣ�ÿһ��Field��Name��Ϊ������key�����Դ����Map��
	 * 
	 * @return
	 */
	public Map<String, Map<String, String>> getFieldConfigs();

	/**
	 * <ul>
	 * �����ṩ��XML�ļ�·�����ṩ��functionName�Զ���ȡfunction��table��field������
	 * <li>���ô˷�����<strong >��Ҫ����
	 * <span style="color:red;">getFunctionConfigs()</span>
	 * ��ȡfunction�ڵ����������</strong></li>
	 * <li><strong >��Ҫ����<span style="color:red;">getTableConfigs()</span>
	 * ��ȡ����table�ڵ�����ԣ�</strong></li>
	 * <li><strong>��Ҫ����<span style="color:red;">getFieldConfigs()</span>
	 * ��ȡ����field�ڵ������</strong>��field <strong>��һ��table�ڵ�洢�ڵ�һλ</strong>��
	 * ���field������һ��table�ڵ㣬������ڷ���ֵ�ĵ�һλ</li>
	 * </ul>
	 * 
	 * @param url
	 *            XML�ļ���·��
	 * @param functionName
	 *            �ṩ��Ҫ��ѯ��function����
	 * @return ��Ѱ�������нڵ�����Զ������ڷ���ֵ��
	 * @throws Exception
	 */
	public void autoLoadXMLFunctionNode(URL url, String functionName) throws Exception;
}
