package com.mypro.manager;

import java.io.FileInputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Xml������
 * @author Leslie Leung
 *
 */
public class XmlManager {
	/**
	 * ��ȡXML������
	 * @param fileName	��Ҫ������xml�ļ�·�����ļ�����������׺�������׺ͳһ��plist,�б�Ҫ���޸ģ�
	 * @param encode	�ַ�������
	 * @return
	 */
	public static XmlPullParser getXmlParser(String fileName,String encode){
		try{
			// ʹ��Ĭ�ϵ���ʽ��ʼ�� XmlPullParserFactory���⿪����������������
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xml = factory.newPullParser();
			xml.setInput(new FileInputStream(fileName + ".plist"), encode);
			return xml;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ��ȡ��ǰ��ǩ��ֵ
	 * 
	 * @return
	 */
	public static String getValueByCurrentTag(XmlPullParser xml) {
		try {
			int eventType = xml.next();
			while (true) {
				// ��ȡ��ǩ����״̬
				if (eventType == XmlPullParser.TEXT) {
					return xml.getText().trim();
				}
				// �ĵ�����״̬
				else if (eventType == XmlPullParser.END_DOCUMENT) {
					// �ĵ������������˳� while ѭ��
					break;
				}
				eventType = xml.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ��ȡָ�����Ƶı�ǩ
	 * 
	 * @return	true:���������ǩ  falseû�������ǩ
	 */
	public static boolean gotoTagByTagName(XmlPullParser xml, String tagName) {
		try {
			int eventType = xml.next();
			String key = null;
			while (true) {
				// ��ǩ��ʼ״̬
				if (eventType == XmlPullParser.START_TAG) {
					key = xml.getName();
					if (key.trim().equals(tagName)) {
						return true;
					}
				}
				// �ĵ�����״̬
				else if (eventType == XmlPullParser.END_DOCUMENT) {
					// �ĵ������������˳� while ѭ��
					return false;
				}
				// �л�����һ��״̬������õ�ǰ״̬������
				eventType = xml.next();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
