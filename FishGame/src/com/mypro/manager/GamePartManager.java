package com.mypro.manager;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import com.mypro.base.graphics.Bitmap;
import com.mypro.base.tools.Log;
import com.mypro.constant.Constant;
import com.mypro.model.BackGround;
import com.mypro.model.GamingInfo;
import com.mypro.model.componets.BottomTime;
import com.mypro.tools.LogTools;

/**
 * ��Ϸ�ؿ�������
 * @author Leslie Leung
 *
 */
public class GamePartManager {
	/**
	 * ����ģʽʹ��
	 */
	private	static GamePartManager manager;
	/**
	 * ���������йؿ�
	 * key		Ϊ�ؿ���
	 * value	Ϊ�ؿ�����
	 */
	private ArrayList<GamePartInfo> games = new ArrayList<GamePartInfo>();
	/**
	 * ��ǰ���еĹؿ�
	 */
	private GamePartInfo part;
	/**
	 * ��ǰ���еĹؿ��ı���ͼƬ
	 */
	private BackGround background;
	/**
	 * �Ƿ�׼�����
	 */
	private boolean prepared;
	/**
	 * ������
	 */
	private GamePartManager(){
		try {
			XmlPullParser xml = XmlManager.getXmlParser("config/GamePart", "UTF-8");
			if (xml != null) {
				initGamePart(xml);
			} else {
				Log.e("GamePartManager", "XML������Ϊ�գ���ʹ��Ĭ��ֵ��ʼ��");
				initDefaultGamePart();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("GamePartManager", "XML����ʧ�ܣ���ʹ��Ĭ��ֵ��ʼ��");
			initDefaultGamePart();
		}
	}
	
	
	/**
	 * ׼��
	 */
	public void prepare(){
		try{
			//���ñ���
			setBg();
			//���������
			FishManager.getFishMananger().updateFish(this.part.getFishName());
			prepared = true;
		}catch(Exception e){
			LogTools.doLogForException(e);
		}
		
	}
	/**
	 * �����ؿ�������
	 */
	public void start(){
		if(!prepared){
			Log.e("GamePartManager", "������û��׼�����Ƿ���ù�prepare������");
			return;
		}

		//������Ⱥ��������֪ͨ�����ɵ���Ⱥ����
		GamingInfo.getGamingInfo().getShoalManager().start(this.part);	
		//����������߳�
		startGiveGoldThrad();
	}
	/**
	 * ������ʱ������߳�
	 */
	private void startGiveGoldThrad(){
		new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					int time = Constant.GIVE_GOLD_TIME;
					BottomTime bt = LayoutManager.getLayoutManager().getBottomTime();
					while(GamingInfo.getGamingInfo().isGaming()){
						while(!GamingInfo.getGamingInfo().isPause()){
							if(time==0){
								giveGold();
								time = Constant.GIVE_GOLD_TIME;
							}
							bt.updateNumIndex(time);
							time--;
							Thread.sleep(1000);
						}
						break;
					}					
				} catch (Exception e) {
					LogTools.doLogForException(e);
				}
				
			}
			private void giveGold(){
				if(GamingInfo.getGamingInfo().getScore()<Constant.GIVE_GOLD_LESS){
					GamingInfo.getGamingInfo().setScore(Constant.GIVE_GOLD);
				}				
			}
		}).start();
	}
	/**
	 * ���ñ���
	 */
	private void setBg(){
		try {
			if (this.part == null) {
				Log.e("GamePartManager", "�ؿ���ϢΪ�գ��޷����ñ���");
				return;
			}
			if(background==null){
				background = new BackGround();
				try {
					Bitmap bgBitmap = ImageManager.getImageMnagaer().getBitmapByAssets(this.part.getBackground());
					if (bgBitmap != null) {
						background.setCurrentPic(ImageManager.getImageMnagaer().sacleImageByWidthAndHeight(bgBitmap, GamingInfo.getGamingInfo().getScreenWidth(), GamingInfo.getGamingInfo().getScreenHeight()));
					} else {
						Log.e("GamePartManager", "����ͼƬ����ʧ�ܣ�·��: " + this.part.getBackground());
					}
				} catch (Exception e) {
					Log.e("GamePartManager", "����ͼƬ����ʧ��: " + e.getMessage());
					LogTools.doLogForException(e);
				}
				GamingInfo.getGamingInfo().getSurface().putDrawablePic(Constant.BACK_GROUND_LAYER, background);
			}else{
				try {
					Bitmap bgBitmap = ImageManager.getImageMnagaer().getBitmapByAssets(this.part.getBackground());
					if (bgBitmap != null) {
						background.setCurrentPic(Bitmap.createScaledBitmap(bgBitmap, GamingInfo.getGamingInfo().getScreenWidth(), GamingInfo.getGamingInfo().getScreenHeight(), false));
					} else {
						Log.e("GamePartManager", "����ͼƬ����ʧ�ܣ�·��: " + this.part.getBackground());
					}
				} catch (Exception e) {
					Log.e("GamePartManager", "����ͼƬ����ʧ��: " + e.getMessage());
					LogTools.doLogForException(e);
				}
			}
		} catch (Exception e) {
			Log.e("GamePartManager", "������������ʧ��: " + e.getMessage());
			LogTools.doLogForException(e);
		}
		
	}
	
	/**
	 * ��ʼ�����еĹؿ�
	 * @param xml	��Ҫ������xml�ļ�
	 */
	private void initGamePart(XmlPullParser xml){
		if (xml == null) {
			Log.e("GamePartManager", "XML������Ϊ�գ���ʹ��Ĭ��ֵ��ʼ��");
			initDefaultGamePart();
			return;
		}
		//ѭ�����еĹؿ�
		while(GamingInfo.getGamingInfo().isGaming()&&XmlManager.gotoTagByTagName(xml, "key")){
			//�����ؿ�������
			GamePartInfo gamePartInfo = new GamePartInfo();
			//��ȡ�ؿ�����
			XmlManager.gotoTagByTagName(xml, "string");
			gamePartInfo.setPartName(XmlManager.getValueByCurrentTag(xml));		
			//��ȡ�ؿ����ֵ���
			XmlManager.gotoTagByTagName(xml, "string");
			String fishNameValue = XmlManager.getValueByCurrentTag(xml);
			if (fishNameValue != null) {
				gamePartInfo.setFishName(fishNameValue.split(";"));
			}
			//��ĳ��ָ���
			XmlManager.gotoTagByTagName(xml, "string");
			String probabilityValue = XmlManager.getValueByCurrentTag(xml);
			if (probabilityValue != null) {
				String probability[] = probabilityValue.split(";");
				int[] showProbability = new int[probability.length];
				for(int i = 0;i<probability.length;i++){
					try {
						showProbability[i] = Integer.parseInt(probability[i]);
					} catch (NumberFormatException e) {
						showProbability[i] = 50;
					}
				}
				gamePartInfo.setShowProbability(showProbability);
			}
			//��ȡ�ɳ��ֵ���Ⱥ����
			XmlManager.gotoTagByTagName(xml, "integer");
			String shoalSumValue = XmlManager.getValueByCurrentTag(xml);
			if (shoalSumValue != null) {
				try {
					gamePartInfo.setShoalSumInScreen(Integer.parseInt(shoalSumValue));
				} catch (NumberFormatException e) {
					gamePartInfo.setShoalSumInScreen(10);
				}
			}
			//��ȡ�ؿ�ʱ��
			XmlManager.gotoTagByTagName(xml, "integer");
			String partTimeValue = XmlManager.getValueByCurrentTag(xml);
			if (partTimeValue != null) {
				try {
					gamePartInfo.setPartTime(Integer.parseInt(partTimeValue));
				} catch (NumberFormatException e) {
					gamePartInfo.setPartTime(60);
				}
			}
			//��ȡ��һ�ص�����
			XmlManager.gotoTagByTagName(xml, "string");
			gamePartInfo.setNextPart(XmlManager.getValueByCurrentTag(xml));
			//��ȡ��������
			XmlManager.gotoTagByTagName(xml, "string");
			gamePartInfo.setBgMusic(XmlManager.getValueByCurrentTag(xml));
			//��ȡ����ͼƬ
			XmlManager.gotoTagByTagName(xml, "string");
			gamePartInfo.setBackground(XmlManager.getValueByCurrentTag(xml));
			this.games.add(gamePartInfo);
		}
		//����йؿ���Ĭ�ϵ�һ��Ԫ��Ϊ��ʼ�ؿ���������ϲ�Ӧ��Ϊ�ռ���
		if(this.games.size()>0){
			this.part = this.games.get(0);
		} else {
			Log.e("GamePartManager", "δ�ӽ����ؿ���Ϣ��ʹ��Ĭ��ֵ��ʼ��");
			initDefaultGamePart();
		}
	}
	
	/**
	 * ��ʼ��Ĭ�ϵĹؿ���Ϣ��XML����ʧ��ʱʹ��
	 */
	private void initDefaultGamePart() {
		GamePartInfo defaultPart = new GamePartInfo();
		defaultPart.setPartName("default");
		defaultPart.setFishName(new String[]{"Fish_01", "Fish_02", "Fish_03"});
		defaultPart.setShowProbability(new int[]{50, 30, 20});
		defaultPart.setShoalSumInScreen(10);
		defaultPart.setPartTime(60);
		defaultPart.setNextPart("default");
		defaultPart.setBgMusic("bgm");
		defaultPart.setBackground("background.jpg");
		this.games.add(defaultPart);
		this.part = defaultPart;
		Log.i("GamePartManager", "ʹ��Ĭ��ֵ��ʼ���ؿ���Ϣ");
	}
	/**
	 * ��ȡ�ؿ�������ʵ��
	 * @return
	 */
	public static GamePartManager getManager(){
		if(manager==null){
			manager = new GamePartManager();
		}
		return manager;
	}
	/**
	 * ע������
	 */
	public void destroy(){
		manager = null;
		System.gc();
	}
	
}
