package com.matalangilbert.twimagej;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.aetrion.flickr.*;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;

public class FlickrImage {
	private static String _apiKey=null;
	private static String _secret=null;
	private final int NUMBER_OF_IMAGES = 400;
	
	private BufferedImage _mediumImage=null;;
	
	public FlickrImage() {
		if (_apiKey == null) {
			LoadProperties();
		}
	}
	
	public BufferedImage getMediumImage() {
		if (_mediumImage==null){
			UpdateImage();
		}
		return _mediumImage;
	}
	
	public BufferedImage getNextMediumImage() {
		UpdateImage();
		return _mediumImage;
	}
	
	public void UpdateImage() {
		try {
			Transport t = new REST();
			Flickr f = new Flickr(_apiKey,_secret,t);
			
			SearchParameters searchParams = new SearchParameters();
			searchParams.setSort(SearchParameters.INTERESTINGNESS_DESC);
			searchParams.setSafeSearch("2");
			String[] tags = new String[]{"happy", 
					"smile",
					"yes"};
			searchParams.setTags(tags);
			
			PhotosInterface photosInterface = f.getPhotosInterface();
			PhotoList photoList = photosInterface.search(searchParams, NUMBER_OF_IMAGES, 1);
			Random rand = new Random();
			
			Photo photo = (Photo)photoList.get(rand.nextInt(NUMBER_OF_IMAGES));
			
			_mediumImage = ImageIO.read(new URL(photo.getMediumUrl()));						
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FlickrException e) {
			e.printStackTrace();
		}
	}
	
	private static void LoadProperties() {
		Properties flickrProperties = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("flickrj.properties");
			flickrProperties.load(in);
			in.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.out.println("Flickr properties file not found");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (!flickrProperties.containsKey("apiKey") || 
					!flickrProperties.containsKey("secret")) {
				System.out.println("Flickr properties not found");
				System.exit(-1);
			} else {
				_apiKey = flickrProperties.getProperty("apiKey");
				_secret = flickrProperties.getProperty("secret");
			}
		}
	}
}