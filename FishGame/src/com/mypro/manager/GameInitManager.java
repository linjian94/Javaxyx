package com.mypro.manager;

import com.mypro.constant.Constant;
import com.mypro.model.GamingInfo;

import com.mypro.tools.LogTools;


/**
 * ��Ϸ��ʼ��������
 * @author Leslie Leung
 *
 */
public class GameInitManager {
	private static GameInitManager manager;
	private boolean initing = true;
	/**
	 * �Ƿ����ڳ�ʼ��
	 * @return
	 */
	public boolean isIniting(){
		return initing;
	}
	private GameInitManager(){}
	
	private void safeSleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
			LogTools.doLogForException(e);
		}
	}
	public static GameInitManager getGameInitManager(){
		if(manager == null){
			synchronized(GameInitManager.class) {
				if(manager == null){
					manager = new GameInitManager();
				}
			}
		}
		return manager;
	}
	public void init(){
		ImageManager imgManager = ImageManager.getImageMnagaer();
		if (imgManager != null) {
			imgManager.initManager();
		}
		initGame();
		safeSleep(500);
		beginGame();
		initing = false;
	}
	/**
	 * ��ʼ���������
	 */
	private void initComponents(){
		LayoutManager.getLayoutManager().init();
	}

	/**
	 * ��ʼ����Ϸ
	 */
	private void initGame(){
		//��ʼ���������
		this.initComponents();
		//��ʼ���÷ֹ�����
		ScoreManager.getScoreManager().init();

		//��ʼ�����ڹ�����
		CannonManager.getCannonManager().init();

		//��ʼ���������
		FishManager.getFishMananger().initFish();

		//��ʼ����Ⱥ������
		GamingInfo.getGamingInfo().setShoalManager(new ShoalManager());

		//��ʼ���ؿ�������
		GamePartManager.getManager().prepare();

		//��ʼ������
		CannonManager.getCannonManager().initCannon();

	}

	/**
	 * ֹͣ��Ϸ
	 */
	public void stop(){
		GamingInfo gamingInfo = GamingInfo.getGamingInfo();
		if (gamingInfo != null) {
			gamingInfo.setGaming(false);
		}
		safeSleep(1000);
		try {
			FishManager.destroy();
		} catch (Exception e) {
			LogTools.doLogForException(e);
		}
		try {
			GamePartManager.getManager().destroy();
		} catch (Exception e) {
			LogTools.doLogForException(e);
		}
		manager = null;
	}

	/**
	 * ��ʼ��Ϸ
	 */
	private void beginGame(){
		//��ʼ
		GamePartManager.getManager().start();
	}

}
