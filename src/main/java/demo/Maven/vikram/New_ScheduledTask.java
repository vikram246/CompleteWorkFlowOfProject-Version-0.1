package demo.Maven.vikram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class New_ScheduledTask {

	New_AttributeMapper MQuery; // Object created for class Multiple Query
	String p_sourceSystemName;

	public New_ScheduledTask(String sourceSystemName) {
		p_sourceSystemName = sourceSystemName;
	}

	public void run() throws InterruptedException, IOException, ParserConfigurationException, SAXException {

		MQuery = new New_AttributeMapper(); // Created a object of Multiple Query class so

		File fh = new File("MyLog.txt");
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ms");
		String lastExeTime = sdf.format(fh.lastModified());
		System.out.println("LastModified " + lastExeTime);
//
//		Date date = new Date();
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ms");
//		String currentExec = formatter.format(date);
//		System.out.println("Date Format with yyyy-MM-dd HH:mm:ss.ms : " + currentExec);
//
//		FileWriter fileWriter = new FileWriter("MyLog.txt");
//		fileWriter.write(currentExec);
//		fileWriter.close();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		org.w3c.dom.Document dom = null;
		Object obj = new Object();
		db = dbf.newDocumentBuilder();
		dom = db.parse(new FileInputStream("configuration.xml"));
		Element docEle = dom.getDocumentElement();
		NodeList sourceSystems_Account = docEle.getElementsByTagName("Properties");
		
		Element sourceSystem_acc = (Element) sourceSystems_Account.item(0);
		Integer query_parameter = Integer.valueOf(sourceSystem_acc.getAttribute("Queries_parameter"));
		
		int i;
		int k = 0;
		ArrayList<New_QueryAccount> objectList = new ArrayList<New_QueryAccount>();
		for (i = 0; i < query_parameter; i++) {
			k = k + 1;
			objectList.add(new New_QueryAccount("Query_" + k,lastExeTime));
			//System.out.println(objectList);
		}
		ArrayList<String> finalResultList = new ArrayList<String>();
		

		for (New_QueryAccount appObject : objectList) {
			appObject.start();
		}
		for (New_QueryAccount appObjectJoin : objectList) {
			appObjectJoin.join();
		}
		for (New_QueryAccount appObjectReturn : objectList) {
			finalResultList.addAll(appObjectReturn.p_accountResultList);
		}

		System.out.println("------------------------------------------");
		System.out.println("Final List"+finalResultList);
		
		HashSet<String> set = new HashSet<String>(finalResultList);

		System.out.println("Final Set" + set);
		
		New_AttributeMapper object = new New_AttributeMapper();

		Stream<String> stream = set.parallelStream();
		stream.forEach((accId) -> {
			object.PrepareCanonicalJSON(p_sourceSystemName, accId);
		});
	}

}
