package com.mypro.manager;

import java.util.ArrayList;
import java.util.HashMap;

import com.mypro.base.graphics.Bitmap;
import com.mypro.constant.Constant;
import com.mypro.model.Ammo;
import com.mypro.model.FishingNet;
import com.mypro.model.GamingInfo;
//import com.mypro.model.WaterRipple;
import com.mypro.model.componets.Cannon;
//import com.mypro.model.componets.ChangeCannonEffect;
import com.mypro.threads.ShotThread;
import com.mypro.tools.LogTools;
import com.mypro.tools.Tool;

/**
 * ���ڹ�����
 * @author Leslie Leung
 *
 */
public class CannonManager {
	/**
	 * �Ƿ���Ը�������
	 */
	private boolean canChangeCannon = true;
	/**
	 * �����ӵ�
	 * key:��������ID��value:�ӵ�ͼƬ����
	 */
	private HashMap<Integer,Bitmap[]> bullet = new HashMap<Integer,Bitmap[]>();
	/**
	 * ���д���
	 * key:��������ID��value:����ͼƬ����
	 */
	private HashMap<Integer,Cannon> cannon = new HashMap<Integer,Cannon>();
	/**
	 * ��������ͼƬ
	 */
	private Bitmap[] net;
	/**
	 * �任���ڵ�Ч��ͼ
	 */
	private Bitmap[] changeCannonEffect;
	/**
	 * �Ƿ���Է����ڵ�
	 */
	private boolean shotable;
	/**
	 * ��ǰʹ�õĴ���ID
	 */
	private int currentCannonIndex = 1;
	private static CannonManager cannonManager;

	
	private CannonManager (){

	}
	/**
	 * ��ʼ�����ڹ�����
	 */
	public void init(){
		try {
			//��ȡ�����ļ�ָ��������ͼƬ
			HashMap<String,Bitmap> allImage = ImageManager.getImageMnagaer().getImagesMapByImageConfig(ImageManager.getImageMnagaer().createImageConfigByPlist("cannon/bulletandnet"),ImageManager.getImageMnagaer().scaleNum);
			allImage.putAll(ImageManager.getImageMnagaer().getImagesMapByImageConfig(ImageManager.getImageMnagaer().createImageConfigByPlist("cannon/fire"),ImageManager.getImageMnagaer().scaleNum));

			//��ʼ���ӵ�
			initAmmo(allImage);
			//��ʼ������
			initNet(allImage);
			//��ʼ������
			initCannon(allImage);
			
		} catch (Exception e) {
			LogTools.doLogForException(e);
		}
	}
	/**
	 * ��ʼ�����д���ͼƬ
	 * @param allImage
	 */
	private void initCannon(HashMap<String,Bitmap> allImage){
		//���ڵ�ͼȫ��(fire_11.png)
		StringBuffer cannonFullName = new StringBuffer();
		//�������ֱ��,�����Ʊ��
		int cannonNum = 1,subCannonNum = 1;
		String cannonName = "fire";
		ArrayList<Bitmap> allCannonList = new ArrayList<Bitmap>();
		//��ȡ��ǰ�ӵ������ж���
		while(GamingInfo.getGamingInfo().isGaming()){
			allCannonList.clear();
			subCannonNum = 1;
			cannonFullName.delete(0, cannonFullName.length());
			cannonFullName.append(cannonName+"_"+cannonNum);
			while(GamingInfo.getGamingInfo().isGaming()){
				Bitmap cannon = allImage.get(cannonFullName.toString()+subCannonNum+".png");
				if(cannon==null){
					break;
				}
				allCannonList.add(cannon);
				subCannonNum++;
			}
			//���û�н�����������
			if(allCannonList.size()==0){
				break;
			}
			//������ת��Ϊ����
			Bitmap[] cannons = new Bitmap[allCannonList.size()];
			for(int i =0;i<allCannonList.size();i++){
				cannons[i] = allCannonList.get(i);
			}
			//�����ڷ����������
			Cannon cannon_obj = new Cannon(cannons);
			cannon_obj.init();
			cannon.put(cannonNum,cannon_obj);
			cannonNum++;
		}
	}
	/**
	 * 初始化大炮
	 */
	public void initCannon(){
		setShotable(false);
		currentCannonIndex = 1;
		Cannon c = getCannon(currentCannonIndex);
		if (c != null) {
			resetCannonMatrix(c);
			LayoutManager.getLayoutManager().initCannon(c);
		}
		setShotable(true);
	}
	/**
	 * ��ʼ������
	 */
	private void initNet(HashMap<String,Bitmap> allImage){
		//������ͼȫ��(net011.png)
		StringBuffer netFullName = new StringBuffer();
		//�������ֱ��
		int netNum = 1;
		String netName = "net";
		ArrayList<Bitmap> allNetList = new ArrayList<Bitmap>();
		//��ȡ��ǰ�ӵ������ж���
		while(GamingInfo.getGamingInfo().isGaming()){
			netFullName.delete(0, netFullName.length());
			netFullName.append(netName+"0"+netNum+".png");
			Bitmap net = allImage.get(netFullName.toString());
			//���û�н�����������
			if(net==null){
				break;
			}
			allNetList.add(net);
			netNum++;
		}
		//������ת��Ϊ����
		net = new Bitmap[allNetList.size()];
		for(int i =0;i<allNetList.size();i++){
			net[i] = allNetList.get(i);
		}
	}
	/**
	 * ��ʼ�������ӵ�ͼƬ
	 */
	private void initAmmo(HashMap<String,Bitmap> allImage){
		//�ӵ���ͼȫ��(bullet12.png),�ӵ�����(bullet12_01.png)
		StringBuffer ammoFullName = new StringBuffer();
		StringBuffer subAmmoFullName = new StringBuffer();
		//�������ֱ��,�����Ʊ��
		int ammoNum = 1,subAmmoNum = 1;
		String ammoName = "bullet";
		ArrayList<Bitmap> allAmmoList = new ArrayList<Bitmap>();
		//��ȡ��ǰ�ӵ������ж���
		while(GamingInfo.getGamingInfo().isGaming()){
			allAmmoList.clear();
			ammoFullName.delete(0, ammoFullName.length());
			ammoFullName.append(ammoName+"0"+ammoNum+".png");
			//����һ�����ڴ���ͼƬ������
			Bitmap ammo = allImage.get(ammoFullName.toString());
			//���ͼƬû���ҵ����˳�ѭ��
			if(ammo==null){
				break;
			}
			allAmmoList.add(ammo);
			subAmmoNum = 1;
			//��ͼ���Կ�����û��ͬ������ͼƬ
			//����-4��ȥ��.png��������ַ����ټ���ƴд������
			ammoFullName.delete(ammoFullName.length()-4, ammoFullName.length());
			while(GamingInfo.getGamingInfo().isGaming()){
				subAmmoFullName.delete(0, subAmmoFullName.length());				
				subAmmoFullName.append(ammoFullName.toString()+"_"+subAmmoNum+".png");
				Bitmap subAmmo = allImage.get(subAmmoFullName.toString());
				if(subAmmo==null){
					break;
				}
				allAmmoList.add(subAmmo);
				subAmmoNum++;
			}
			//������ת��Ϊ����
			Bitmap[] bullets = new Bitmap[allAmmoList.size()];
			for(int i =0;i<allAmmoList.size();i++){
				bullets[i] = allAmmoList.get(i);
			}
			//���ӵ������������
			bullet.put(ammoNum, bullets);
			ammoNum++;
		}
	}
	
	public static CannonManager getCannonManager(){
		if(cannonManager==null){
			cannonManager = new CannonManager();
		}
		return cannonManager;
	}
	/**
	 * ���ݸ�������ID��ȡ����Ķ�Ӧ�ӵ���ʵ��
	 * @param id
	 * @return
	 */
	private Ammo getAmmo(int id){
		Ammo ammo = new Ammo(id);
		ammo.setCurrentPic(this.bullet.get(id),new FishingNet(this.net[id-1],ammo));
		return ammo;
	}
	/**
	 * ���ݸ�������ID��ȡ���ڵ�ʵ��
	 * @param id
	 * @return
	 */
	private Cannon getCannon(int id){
		return this.cannon.get(id);
		
	}
	/**
	 * ��ߴ��ڵȼ�
	 */
	public void upCannon(){
		if(!canChangeCannon){
			return;
		}
		canChangeCannon = false;//������������
		setShotable(false);
		if(currentCannonIndex+1>cannon.size()){
			currentCannonIndex = 1;
		}else{
			currentCannonIndex++;
		}
		resetCannonMatrix(getCannon(currentCannonIndex));
//		playChangeCannonEffect();
		//���Ÿ������ڵ���Ч
//		SoundManager.playSound(SoundManager.SOUND_BGM_CHANGE_CANNON);
		LayoutManager.getLayoutManager().updateCannon(getCannon(currentCannonIndex));	
		canChangeCannon = true;
		setShotable(true);
	}
	/**
	 * ���ʹ��ڵȼ�
	 */
	public void downCannon(){
		if(!canChangeCannon){
			return;
		}
		canChangeCannon = false;//������������
		setShotable(false);
		if(currentCannonIndex-1==0){
			currentCannonIndex = cannon.size();
		}else{
			currentCannonIndex--;
		}
		resetCannonMatrix(getCannon(currentCannonIndex));

		LayoutManager.getLayoutManager().updateCannon(getCannon(currentCannonIndex));	
		canChangeCannon = true;
		setShotable(true);
	}

	/**
	 * ����ӵ�
	 * @param targetX		Ŀ���X����
	 * @param targetY		Ŀ���y����
	 */
	public void shot(float targetX,float targetY){
		if(!shotable || GamingInfo.getGamingInfo().getScore() < currentCannonIndex){
			return;
		}
		waitReload();
		GamingInfo.getGamingInfo().setScore(GamingInfo.getGamingInfo().getScore()-currentCannonIndex);
		Cannon c = getCannon(currentCannonIndex);
		if (c != null) {
			this.rotateCannon(targetX, targetY, c);
			c.shot();
		}
		Ammo ammo = getAmmo(currentCannonIndex);
		if (ammo != null) {
			ShotThread st = new ShotThread(targetX-ammo.getPicWidth()/2, targetY-ammo.getPicHeight()/2, ammo, GamingInfo.getGamingInfo().getCannonLayoutX()-ammo.getPicWidth()/2, GamingInfo.getGamingInfo().getCannonLayoutY()-ammo.getPicHeight()/2);
			st.start();
		}
	}
	/**
	 * �ϵ�ʱ��
	 */
	private void waitReload(){
		new Thread(new Runnable() {		
			@Override
			public void run() {
				try {
					shotable = false;
					Thread.sleep(Constant.CANNON_RELOAD_TIME);
					shotable = true;
				} catch (Exception e) {
					LogTools.doLogForException(e);
				}
				
			}
		}).start();
		
		
	}
	/**
	 * 旋转大炮
	 */
	private void rotateCannon(float targetX,float targetY,Cannon cannon){
		if (cannon == null) {
			return;
		}
		try{
			float gun_angle = Tool.getAngle(targetX, targetY,GamingInfo.getGamingInfo().getScreenWidth()/2,GamingInfo.getGamingInfo().getScreenHeight());
			cannon.getPicMatrix().reset();
			cannon.getPicMatrix().setTranslate(cannon.getX(), cannon.getY());
			if(gun_angle>=90){
				cannon.getPicMatrix().preRotate(90-gun_angle,cannon.getGun_rotate_point_x(),cannon.getGun_rotate_point_y());
			}else{
				cannon.getPicMatrix().preRotate(-(gun_angle-90),cannon.getGun_rotate_point_x(),cannon.getGun_rotate_point_y());			
			}		
		}catch(Exception e){
			LogTools.doLogForException(e);
		}
	}
	/**
	 * �ָ����ڵĳ�ʼ״̬
	 */
	private void resetCannonMatrix(Cannon cannon){
		rotateCannon(GamingInfo.getGamingInfo().getScreenWidth()/2,0,cannon);
	}
	/**
	 * �����Ƿ������������
	 * @param shotable	true:���� false:������
	 */
	public void setShotable(boolean shotable){
		this.shotable = shotable;
	}
}
