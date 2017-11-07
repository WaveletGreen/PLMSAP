package util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class KHXMLUtil implements Inf_KHXMLUtil {
	private Document document;
	private Map<String, String> FunctionConfigs = new LinkedHashMap<String, String>();
	private Map<String, Map<String, String>> TableConfigs = new HashMap<String, Map<String, String>>();
	private Map<String, Map<String, String>> FieldConfigs = new LinkedHashMap<String, Map<String, String>>();
	private Map<String, Map<String, String>> inputParam = new LinkedHashMap<String, Map<String, String>>();
	private Element eleFunction;
	private ArrayList<Element> eleTable = new ArrayList<Element>();
	
	
	public Document getDocument() {
		return document;
	}

	public Map<String, Map<String, String>> getInputParam() {
		return inputParam;
	}

	public Element getEleFunction() {
		return eleFunction;
	}

	public ArrayList<Element> getEleTable() {
		return eleTable;
	}

	public Map<String, String> getFunctionConfigs() {
		return FunctionConfigs;
	}

	public Map<String, Map<String, String>> getTableConfigs() {
		return TableConfigs;
	}

	public Map<String, Map<String, String>> getFieldConfigs() {
		return FieldConfigs;
	}

	public KHXMLUtil() {
		super();
	}

	/**
	 * ͨ������XML�ļ���·��������XML�ļ�����ȡdocument����
	 * 
	 * @param url
	 *            XML�ļ���·��
	 * @return �����Ѿ�����XML�ļ����document����
	 * @throws DocumentException
	 */
	@Override
	public Document parse(URL url) throws DocumentException {
		SAXReader reader = new SAXReader();
		this.document = reader.read(url);
		return document;
	}

	@Override
	public Map<String, String> getFunctionElementConfigs(Document document, String functionName) throws Exception {
		Map<String, String> functionResult = new HashMap<String, String>();
		if ("".equals(functionName) || null == functionName) {
			System.err.println("FunctionName����Ϊ��");
			return null;
		}
		Element root = document.getRootElement();
		String name = null;
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element element = it.next();
			Attribute a = element.attribute("Name");
			name = a.getValue();
			if ((null != name || !"".equals(name)) && functionName.equals(a.getValue())) {
				functionResult.clear();
				for (Attribute attr : element.attributes()) {
					functionResult.put(attr.getName(), attr.getValue());
				}
				this.eleFunction = element;
				this.FunctionConfigs = functionResult;
				return functionResult;
			}

		}
		if ((null == name || "".equals(name))) {
			throw new Exception("û���ҵ�Ŀ��Function");
		}
		return null;

	}

	public Map<String, Map<String, String>> getTableElementConfigs(Element function) throws Exception {
		return getTableElementConfigs(function, null);
	}

	@Override
	public Map<String, Map<String, String>> getTableElementConfigs(Element function, String tableName)
			throws Exception {
		String name = null;
		// ���δ�ṩtableName��������function�ڵ��µ�����table
		if (null == tableName) {
			for (Iterator<Element> it = function.elementIterator(); it.hasNext();) {
				Element table = it.next();
				Attribute a = table.attribute("Name");
				name = a.getValue();
				if ("FieldConfig" == table.getName()) {
					Map<String, String> inputParamResult = new HashMap<String, String>();
					for (Attribute attr : table.attributes()) {
						inputParamResult.put(attr.getName(), attr.getValue());
					}
					inputParam.put(table.attribute("Name").getValue(), inputParamResult);
				} else {
					if ((null == name || "".equals(name))) {
						throw new Exception("������һ��Tableδ�ṩName����");
					} else if (!"".equals(name) && null != name) {
						Map<String, String> tableResult = new HashMap<String, String>();
						for (Attribute attr : table.attributes()) {
							tableResult.put(attr.getName(), attr.getValue());
						}
						this.eleTable.add(table);
						this.TableConfigs.put(name, tableResult);
					}

				}
			}
		}
		// ���tableName��Ϊ�գ�������ƥ���table
		else {
			for (Iterator<Element> it = function.elementIterator(); it.hasNext();) {
				Element table = it.next();
				Attribute a = table.attribute("Name");
				if ("Table" != table.getName())
					continue;
				name = a.getValue();
				if ((null == name || "".equals(name))) {
					throw new Exception("������һ��Tableδ�ṩName����");
				} else if ((null != tableName && !"".equals(table)) && (!"".equals(name) && null != name)
						&& tableName.equals(name)) {
					Map<String, String> tableResult = new HashMap<String, String>();
					for (Attribute attr : table.attributes()) {
						tableResult.put(attr.getName(), attr.getValue());
					}
					this.eleTable.add(table);
					this.TableConfigs.put(name, tableResult);
				}
			}
		}
		if ((null == name || "".equals(name))) {
			throw new Exception("������һ��Tableδ�ṩName����");
		}
		return TableConfigs;
	}

	@Override
	public void searchFieldConfigs(Element element) throws DocumentException {
		searchFieldConfigs(element, false);
	}

	/* ��ȡ��Table���������FieldConfig */

	public void searchFieldConfigs(Element element, boolean isAuto) throws DocumentException {
		if (!isAuto) {
			// �������index����
			FieldConfigs.clear();
		}
		if (null == element) {
			System.err.println("�ṩ�Ľڵ�Ϊ�գ����ṩ����ڵ�");
			return;
		}
		sunBar(element);
	}

	private void sunBar(Element element) throws DocumentException {

		List<Attribute> attrList = element.attributes();
		Map<String, String> fieldPros = new HashMap<String, String>();
		for (Attribute attribute : attrList) {
			fieldPros.put(attribute.getName(), attribute.getValue());
		}
		FieldConfigs.put(element.attribute("Name").getValue(), fieldPros);
		for (Iterator<Element> it = element.elementIterator(); it.hasNext();) {
			Element ele = it.next();
			sunBar(ele);
		}

	}

	@Override
	public void autoLoadXMLFunctionNode(URL url, String functionName) throws Exception {
		this.parse(url);
		this.getFunctionElementConfigs(document, functionName);
		FieldConfigs.clear();
		getTableElementConfigs(eleFunction);
		for (Element element : eleTable) {
			searchFieldConfigs(element, true);
		}
		for (String key : TableConfigs.keySet()) {
			FieldConfigs.remove(key);
		}
	}
}
