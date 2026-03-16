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
	public static GameInitManager getGameInitManager(){
		if(manager == null){
			manager = new GameInitManager();
		}
		return manager;
	}
	public void init(){
		try {
			ImageManager.getImageMnagaer().initManager();

			initGame();//��ʼ����Ϸ
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				LogTools.doLogForException(e);
			}

			beginGame();
		} catch (Exception e) {
			Log.e("GameInitManager", "��Ϸ��ʼ��ʧ��: " + e.getMessage());
			LogTools.doLogForException(e);
		}
		initing = false;
	}
	/**
	 * ��ʼ���������
	 */
	private void initComponents(){
		try {
			LayoutManager layoutManager = LayoutManager.getLayoutManager();
			if (layoutManager != null) {
				layoutManager.init();
			}
		} catch (Exception e) {
			Log.e("GameInitManager", "initComponentsʧ��: " + e.getMessage());
			LogTools.doLogForException(e);
		}
	}

	/**
	 * ��ʼ����Ϸ
	 */
	private void initGame(){
		try {
			//��ʼ���������
			this.initComponents();
			//��ʼ���÷ֹ�����
			ScoreManager scoreManager = ScoreManager.getScoreManager();
			if (scoreManager != null) {
				scoreManager.init();
			}

			//��ʼ�����ڹ�����
			CannonManager cannonManager = CannonManager.getCannonManager();
			if (cannonManager != null) {
				cannonManager.init();
			}

			//��ʼ���������
			FishManager fishManager = FishManager.getFishMananger();
			if (fishManager != null) {
				fishManager.initFish();
			}

			//��ʼ����Ⱥ������
			GamingInfo gamingInfo = GamingInfo.getGamingInfo();
			if (gamingInfo != null) {
				gamingInfo.setShoalManager(new ShoalManager());
			}

			//��ʼ���ؿ�������
			GamePartManager gamePartManager = GamePartManager.getManager();
			if (gamePartManager != null) {
				gamePartManager.prepare();
			}

			//��ʼ������
			if (cannonManager != null) {
				cannonManager.initCannon();
			}
		} catch (Exception e) {
			Log.e("GameInitManager", "initGameʧ��: " + e.getMessage());
			LogTools.doLogForException(e);
		}

	}

	/**
	 * ֹͣ��Ϸ
	 */
	public void stop(){

		try {
			//������Ϸ����
			GamingInfo gamingInfo = GamingInfo.getGamingInfo();
			if (gamingInfo != null) {
				gamingInfo.setGaming(false);
			}
			Thread.sleep(1000);

			//ע���������
			FishManager.destroy();
			//ע����Ϸ�ؿ�������
			GamePartManager gamePartManager = GamePartManager.getManager();
			if (gamePartManager != null) {
				gamePartManager.destroy();
			}

			//ע���Լ�
			manager = null;
		} catch (Exception e) {
			Log.e("GameInitManager", "stopʧ��: " + e.getMessage());
			LogTools.doLogForException(e);
		}
	}

	/**
	 * ��ʼ��Ϸ
	 */
	private void beginGame(){
		try {
			//��ʼ
			GamePartManager gamePartManager = GamePartManager.getManager();
			if (gamePartManager != null) {
				gamePartManager.start();
			}
		} catch (Exception e) {
			Log.e("GameInitManager", "beginGameʧ��: " + e.getMessage());
			LogTools.doLogForException(e);
		}
	}

}
