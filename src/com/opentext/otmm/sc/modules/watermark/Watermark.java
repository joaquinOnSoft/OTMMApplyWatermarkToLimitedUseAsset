package com.opentext.otmm.sc.modules.watermark;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Watermark {

	protected static final Log log = LogFactory.getLog(Watermark.class);

	/**
	 * Add a watermark to an image.
	 * @param input - Image input file 
	 * @param text - Text to be used to add a watermark to the image
	 * @return file with the watermark applied.
	 * 
	 * @see https://legacy.imagemagick.org/discourse-server/viewtopic.php?t=19177
	 */
	public File apply(File input, String text) {
		File watermarkedImg = null;
		
		log.debug("Watermark input file path: " + input.getAbsolutePath());
		
		try {
			String inputPath = input.getAbsolutePath();
			String outputFileName = input.getName(); //getRandomName(getFileExtension(inputPath));
			
			// Lets add a watermark to the original image 
			// Command will look like:
			// 
			// C:\Apps\DMTS\ImageMagick\magick.exe  image.jpg -pointsize 50 -font Arial -fill rgba\(0,0,0,0.4\) -gravity center -annotate +0+0 "# downloads exceeded" image_2.jpg

			// TODO Generate the watermarked asset in the repository folder, not in the working directory.
			watermarkedImg = new File("c:\\temp\\watermark", outputFileName);
			String watermarkCommand = "C:\\Apps\\DMTS\\ImageMagick\\magick.exe " + inputPath
					+ " -colorspace RGB -pointsize 50 -font Arial -fill rgba\\(0,0,0,0.4\\) -gravity center -annotate +0+0 \"" + text 
					+ "\" " + watermarkedImg.getAbsolutePath();
			log.debug("Watermark command: " + watermarkCommand);

			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.command("cmd.exe", "/c", watermarkCommand);
			processBuilder.start();

		} catch (Exception e) {
			log.error("Adding watermark: ", e);
		}
		
		return watermarkedImg;
	}
}
