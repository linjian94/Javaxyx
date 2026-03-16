package com.mypro.mainsurface;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import com.mypro.base.graphics.Bitmap;
import com.mypro.base.graphics.Canvas;
import com.mypro.base.graphics.Matrix;
import com.mypro.base.graphics.Paint;
import com.mypro.basecomponet.JMatrix;
import com.mypro.constant.Constant;
import com.mypro.model.GamingInfo;
import com.mypro.model.interfaces.Drawable;

public class MainSurface extends JPanel{
	/**
	 * �޸�ͼ��Ĳ�������
	 */
	//����ͼ��
	private final static int CHANGE_MODE_UPDATE = 0;
	//����Ԫ�ص�ͼ��
	private final static int CHANGE_MODE_ADD = 1;
	//ɾ��Ԫ�ش�ͼ��
	private final static int CHANGE_MODE_REMOVE = 2;
	// ͼƬ��ͼ��ֲ�
	private HashMap<Integer, ArrayList<Drawable>> picLayer =new HashMap<Integer, ArrayList<Drawable>>();
	// �޸ĺ��ͼƬ��ͼ��ֲ�,������ݲ�����Ϊ������ͼ�㣬�ֱ������ӵ�Ԫ�أ���ɾ����Ԫ��
	private HashMap<Integer, ArrayList<Drawable>> addPicLayer = new HashMap<Integer, ArrayList<Drawable>>(),removePicLayer = new HashMap<Integer, ArrayList<Drawable>>();
	// �Ƿ��޸Ĺ�ͼ��
	private boolean changeLayer = false;
	private int picLayerId[] = new int[0]; // ����һ��ͼ��ID�����ٻ�ȡͼ����ƣ�ʡȥ�˴�map�л�ȡ����ͼ���������⣩
	private Paint paint; // ����
	private OnDrawThread odt; // ��Ļ�����̣߳����ڿ��ƻ���֡���������Ե���onDraw����
	public MainSurface() {
		setSize(GamingInfo.getGamingInfo().getScreenWidth(), GamingInfo.getGamingInfo().getScreenHeight());
		paint = canvas.getPaint();
		paint.setAntiAlias(true);//���ÿ����
		paint.setDither(true);
		odt = new OnDrawThread(this);
		
	}
	public void action() throws Exception {
		odt.start();
	}
	/**
	 * ��ͼ������������������߳̿��ƣ������Ե��õ�
	 */
	public void onDraw(Canvas canvas) {
		//����ͼ������
		updatePicLayer(CHANGE_MODE_UPDATE,0,null);
		
		// ��������ͼ�㣬��ͼ���Ⱥ�˳�����
		for (int id : picLayerId) {
				for (Drawable drawable : picLayer.get(id)) {
					drawable.onDraw(canvas, paint);
				}
		}
	}
	/**
	 * ����ͼ�㣬�����Ϊ���ֲ������ֱ��Ǹ�����ʱͼ���е����ݵ�����ͼ���У�ɾ������ͼ���е�Ԫ�أ����ӻ���ͼ���е�Ԫ��
	 * ������˸��߳�������֤���߳��²���ͼ��İ�ȫ��
	 * @param mode	�Ի���ͼ��Ĳ������ͣ���Ӧ��ǰ���CHANGE_MODE����
	 * @param layerId	������ͼ��ID
	 * @param draw		������ͼ��Ԫ��
	 */
	private synchronized void updatePicLayer(int mode,int layerId,Drawable draw){
		switch(mode){
		//����ʱͼ���е����ݸ���������ͼ����
		case CHANGE_MODE_UPDATE:
			//������޸�
			if(changeLayer){
				//��ͼ�������µ�Ԫ��
				for(Integer id:addPicLayer.keySet()){
					for(Drawable d:addPicLayer.get(id)){
						//���Ҫ���ӵ�Ԫ������ͼ�㲻���ڣ��򴴽����ͼ�㣬������ͼ��ID����
						if(this.picLayer.get(id)==null){
							this.picLayer.put(id, new ArrayList<Drawable>());
							updateLayerIds(id);
						}
						this.picLayer.get(id).add(d);
					}
				}
				addPicLayer.clear();
				//ɾ��ͼ���е�Ԫ��
				for(Integer id:removePicLayer.keySet()){
					for(Drawable d:removePicLayer.get(id)){
						try {
							this.picLayer.get(id).remove(d);
						} catch (Exception e) {
							System.out.println("ͼ�����ݲ�����:"+id);
						}
						
					}
				}
				removePicLayer.clear();
				changeLayer = false;			
			}
			break;
		/**
		 * ���������ͼͼ�������ӻ���ɾ��Ԫ�أ�������ֱ�Ӳ�������ͼ�㣬���Ǵ���ڶ�Ӧ����ʱͼ���У��ȴ����Ʒ������������н��仯�����ݸ��µ�����ͼ����
		 * ��֤���̲߳�������µİ�ȫ��
		 */
		//����һ��Ԫ��
		case CHANGE_MODE_ADD:
			ArrayList<Drawable> al = addPicLayer.get(layerId);
			if(al==null){
				al = new ArrayList<Drawable>();
				addPicLayer.put(layerId, al);
			}
			al.add(draw);
			changeLayer = true;	
			break;
		//ɾ��һ��Ԫ��
		case CHANGE_MODE_REMOVE:
			ArrayList<Drawable> al1 = removePicLayer.get(layerId);
			if(al1==null){
				al1 = new ArrayList<Drawable>();
				removePicLayer.put(layerId, al1);
			}
			al1.add(draw);
			changeLayer = true;	
			break;
		}
		
	}
	
	/**
	 * ��һ���ɻ��Ƶ�ͼ����ͼ����
	 * 
	 * @param layer
	 *            ͼ��� ͼ�����Ȼ��int������ʵ����ֻ֧�ֵ�byte��ԭ����ͼ��û�б�Ҫ��ô��
	 * @param pic
	 *            �ɻ��Ƶ�ͼ
	 */
	public void putDrawablePic(int layer, Drawable pic) {
		if(pic==null){
			System.out.println("ͼ�����ݲ���Ϊ��:��Ӧͼ��:"+layer);
			return;
		}
		updatePicLayer(CHANGE_MODE_ADD,layer,pic);
	}

	/**
	 * ��һ���ɻ��Ƶ�ͼ��ͼ�����Ƴ�
	 * 
	 * @param layer
	 * @param pic
	 */
	public void removeDrawablePic(int layer, Drawable pic) {
		if(pic==null){
			System.out.println("ͼ�����ݲ���Ϊ��:��Ӧͼ��:"+layer);
			return;
		}
		updatePicLayer(CHANGE_MODE_REMOVE,layer,pic);
	}

	/**
	 * ����ͼ��Id
	 * 
	 * @param newLayerId
	 */
	private void updateLayerIds(int newLayerId) {
		// ��ʼ��ͼ��
		if (picLayerId.length == 0) {
			picLayerId = new int[1];
			picLayerId[0] = newLayerId; // ���µ�ͼ��ID���ӵ���ʼ����ͼ��ID������
		} else {
			// ����һ���µ�ͼ�����飬���ȱ�ԭ���Ĵ�1λ
			int picLayerIdFlag[] = new int[picLayerId.length + 1];
			for (int i = 0; i < picLayerId.length; i++) {
				// �������������µ�ͼ��IDС�ڵ�ǰͼ��ID�����µ�ͼ��ID��������
				if (picLayerId[i] > newLayerId) {
					for (int f = picLayerIdFlag.length - 1; f > i; f--) {
						picLayerIdFlag[f] = picLayerId[f - 1];
					}
					picLayerIdFlag[i] = newLayerId;
					break;
				} else {
					picLayerIdFlag[i] = picLayerId[i];
				}
				// ���������󣬶�û�б���ͼ��ID��ģ��ͽ��µ�ͼ��ID�������
				if (i == picLayerId.length - 1) {
					picLayerIdFlag[picLayerIdFlag.length - 1] = newLayerId;
				}
			}
			// ���µ�ͼ��ID���鸲��ԭ�е�
			this.picLayerId = picLayerIdFlag;
		}
	}
	//����
	private JCanvas canvas = new JCanvas();
	@Override
	public synchronized void paint(Graphics g) {
			g.drawImage(canvas.getCanvas(), 0, 0, null);		
	}
	
	public synchronized Canvas lockCanvas() {
			return canvas;		
	}

	
	public void unlockCanvasAndPost(Canvas canvas) {
		repaint();		
	}
	
	/**
	 * ������
	 * @author Xiloer
	 *
	 */
	private class JCanvas implements Canvas{		
		/*
		 * ���ƻ���
		 */
		private BufferedImage canvas = new BufferedImage(GamingInfo.getGamingInfo().getScreenWidth(), GamingInfo.getGamingInfo().getScreenHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		private JPaint paint = new JPaint(canvas);
		
		public Paint getPaint() {
			return paint;
		}
		
		
		public BufferedImage getCanvas() {
			return canvas;
		}


		@Override
		public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
			if (bitmap == null) {
				System.out.println("drawBitmapʧ��: bitmapΪnull");
				return;
			}
			if (matrix == null) {
				System.out.println("drawBitmapʧ��: matrixΪnull");
				return;
			}
			this.paint.getGraphics().drawImage(bitmap.getImage(),((JMatrix)matrix).trans, null);
		}
		@Override
		public void drawBitmap(Bitmap bitmap, float x, float y, Paint paint) {
			if (bitmap == null) {
				System.out.println("drawBitmapʧ��: bitmapΪnull");
				return;
			}
			this.paint.getGraphics().drawImage(bitmap.getImage(),(int)x,(int)y, null);			
		}	
		
		public class JPaint implements Paint{
			Graphics2D graphics;
			public JPaint(BufferedImage canvas){
				graphics = (Graphics2D)canvas.getGraphics();
			}
			
			@Override
			public void setTypeface(Object obj) {
			}

			public Graphics2D getGraphics() {
				return graphics;
			}

			@Override
			public void setAntiAlias(boolean tf) {
				if(tf){
					RenderingHints qualityHints = graphics.getRenderingHints();
					if(qualityHints==null){
						qualityHints = new  RenderingHints(RenderingHints.KEY_ANTIALIASING,              
						  		RenderingHints.VALUE_ANTIALIAS_ON);
						graphics.setRenderingHints(qualityHints);
					}else{
						qualityHints.put(RenderingHints.KEY_ANTIALIASING,               
						  		RenderingHints.VALUE_ANTIALIAS_ON); 
					}				
					qualityHints.put(RenderingHints.KEY_RENDERING,               
							RenderingHints.VALUE_RENDER_QUALITY); 
					 
					qualityHints.put(RenderingHints.KEY_INTERPOLATION,               
							RenderingHints.VALUE_INTERPOLATION_BICUBIC);
					qualityHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,               
							RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
					qualityHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,               
							RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				}
			}

			@Override
			public void setFilterBitmap(boolean tf) {
			}

			@Override
			public void setDither(boolean tf) {
				if(tf){
					RenderingHints qualityHints = graphics.getRenderingHints();
					if(qualityHints==null){
						qualityHints = new  RenderingHints(RenderingHints.KEY_DITHERING,              
						  		RenderingHints.VALUE_DITHER_ENABLE);
						graphics.setRenderingHints(qualityHints);
					}else{
						qualityHints.put(RenderingHints.KEY_DITHERING,               
						  		RenderingHints.VALUE_DITHER_ENABLE); 
					}	
					qualityHints.put(RenderingHints.KEY_RENDERING,               
							RenderingHints.VALUE_RENDER_QUALITY);
				}
			}

			@Override
			public void setTextSize(int size) {
			}

			@Override
			public void setColor(int color) {
			}
			
		}
	}
	
	public class OnDrawThread extends Thread{
		private MainSurface surface;
		private int drawSpeed;//ÿ�λ��ƺ����Ϣ�����������ֵ�Ǹ��ݳ����еĻ���֡��������
		public OnDrawThread(MainSurface surface){
			super();
			this.surface = surface;
			drawSpeed = 1000/Constant.ON_DRAW_SLEEP;
		}
		
		public void run(){
			super.run();
			Canvas canvas = null;
			while(GamingInfo.getGamingInfo().isGaming()){
				try{
					canvas = lockCanvas();
//					synchronized (this.sh) {
						if(canvas!=null){
							surface.onDraw(canvas);
						}
//					}
				}catch(Exception e){
//					Log.e(this.getName(), e.toString());
					e.printStackTrace();
				}finally{
					try{
						unlockCanvasAndPost(canvas);
					}catch(Exception e){
//						Log.e(this.getName(), e.toString());
					}
				}
				try{
					Thread.sleep(drawSpeed);
				}catch(Exception e){
					
				}
			}
		}
	}
}
