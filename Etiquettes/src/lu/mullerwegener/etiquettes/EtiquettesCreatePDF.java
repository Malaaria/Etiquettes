package lu.mullerwegener.etiquettes;

import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPageable;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.Code128Constants;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class EtiquettesCreatePDF {

	private String service;
	private String compteur;
	private int nbpages;
	private PDDocument doc;	

	private float decalage_droite = 0f;
	private float decalage_bas = 0f;
	private float orig_x = 35f;
	private float orig_y = 760f;
	private float distance_droite = 140f;
	private float distance_bas = -48f;

	public EtiquettesCreatePDF(){

	

	}

	protected void generateBarcode(Etiquettes etiq, String pathImg, String pannee){
		Code128Bean bean = new Code128Bean();
		bean.setCodeset(Code128Constants.CODESET_B);
		int dpi = 150;
		bean.setModuleWidth(UnitConv.in2mm(5.0f / dpi));
		bean.setBarHeight(UnitConv.in2mm(120.0f / dpi));
		bean.setFontSize(10);

		//bean.setWideFactor(3);
		bean.doQuietZone(false);

		File bitmapBarcode = new File(pathImg);
		FileOutputStream bos;
		try {
			bos = new FileOutputStream(bitmapBarcode);
			BitmapCanvasProvider bcp = new BitmapCanvasProvider(bos, "image/png", 300, BufferedImage.TYPE_BYTE_BINARY, true, 0);			
			bean.generateBarcode(bcp, pannee + service + etiq.getCompteur());
			bcp.finish();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		//float posX = (orig_x + decalage_droite); 
		//float posY = orig_y + decalage_bas;
		//System.out.println("Currently generating : " + "2016" + service + etiq.getCompteur() + " with this  : " + posX + ", " + posY);
		etiq.incrementCompteur();
		//pb.setForeground(Color.BLUE);		
	}
	
	protected void trait(String pservice, String pcompteur, String pNbPages, String pannee) throws IOException, InterruptedException {
		service = pservice;
		compteur = pcompteur;
		nbpages = Integer.valueOf(pNbPages);

		Etiquettes etiq = new Etiquettes(compteur);
		String pathImg = "barcodeBitmap.png";

		doc = new PDDocument();
		int page_en_cours = 0;
		while(page_en_cours < nbpages){
			initPage();
			page_en_cours ++;
			PDPage page = new PDPage(PDRectangle.A4);
			doc.addPage(page);

			//PDFont font = PDType1Font.HELVETICA_BOLD;

			try {
				float scale = 0.08f; 
				PDPageContentStream contents = new PDPageContentStream(doc, page);									

				for(int _i0=0;_i0<16;_i0++){
					for(int _j0=0;_j0<4;_j0++){
						generateBarcode(etiq, pathImg, pannee);
						PDImageXObject pdImage = PDImageXObject.createFromFile(pathImg, doc);
						contents.drawImage(pdImage, orig_x + decalage_droite, orig_y + decalage_bas, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
						decalage_droite = decalage_droite + distance_droite;											
					}
					decalage_droite = 0;
					decalage_bas = decalage_bas + distance_bas;
				}
				contents.close();
				//this.doc.save("test.pdf");
			} catch (IOException e) {
				e.printStackTrace();				
			}
		}
		
		compteur = etiq.getCompteur();
	}

	protected void initPage(){
		this.decalage_droite = 0f;
		this.decalage_bas = 0f;
		this.orig_x = 35f;
		this.orig_y = 760f;
		this.distance_droite = 140f;
		this.distance_bas = -48f;
	}
	
	protected String getCompteur(){
		return this.compteur;
	}

	protected boolean print() throws IOException, PrinterException{
		boolean result = true;
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPageable(new PDFPageable(this.doc));
		if (job.printDialog()){
			job.print();
		}else{
			result = false;
		}
		return result;
	}
}
