package jwc.com.control;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jwc.com.httphandler.HttpHandler;
import jwc.com.model.DeliveryServiceModel;
@RestController
@RequestMapping("api")

public class DeliveryServiceControl {
	
	HttpHandler _httpHandler=new HttpHandler();
	Document doc=null;
	@GetMapping("/Condition")
	public String getCondition()
	{
		return "Good Condition";
	}
	
	@PostMapping(value="/SearchDelivery")
	public String SearchDelivery(@RequestBody DeliveryServiceModel serviceModel) throws IOException
	{
		String serviceName=serviceModel.getServiceName();
		String invoiceNumber=serviceModel.getInvoiceNumber();
		
		String result="";
		switch(serviceName)
		{
			case "rozen":
				result=SearchRozen(invoiceNumber);
				break;
			case "hanjin":
				SearchHanjin(invoiceNumber);
				break;
			case "CJ":
				SearchCJ(invoiceNumber);
				break;
			case "Lotte":
				SearchLotte(invoiceNumber);
				break;
			default:
				break;
		}
		return result;
	}
	
	public String SearchRozen(String invoiceNumber) throws IOException 
	{
		/*
		 * {
			"serviceName":"rozen",
			"invoiceNumber":"33212191880"
			}
		 */
		//invoiceNumber : 운송장 번호
		String baseURL="https://www.ilogen.com";
		String path="";
		String reData="";
		String postData="";
		
		//Condition Check
		//Set Header
		path="/web/personal/tkSearch";
		_httpHandler.AddRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		_httpHandler.AddRequestHeader("Accept-Encoding","gzip, deflate, br");
		_httpHandler.AddRequestHeader("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
		_httpHandler.AddRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
		//%EB%A1%9C%EC%A0%A0%ED%83%9D%EB%B0%B0 => URL Decode => 로젠택배
		_httpHandler.AddRequestHeader("Referer","https://search.naver.com/search.naver?where=nexearch&sm=tab_jum&query=%EB%A1%9C%EC%A0%A0%ED%83%9D%EB%B0%B0");
		_httpHandler.AddRequestHeader("Connection","keep-alive");
		_httpHandler.AddRequestHeader("Host","www.ilogen.com");
		
		_httpHandler.Send(baseURL+path);
		reData=_httpHandler.getResponseText();
		
		
		//Send Data
		_httpHandler.AddRequestHeader("Referer", "https://www.ilogen.com/web");
		path="/web/personal/trace/"+invoiceNumber;
		_httpHandler.Send(baseURL+path);
		reData=_httpHandler.getResponseText();
		doc=Jsoup.parse(reData);
		
		JSONArray deliveryInfoJsonArray=new JSONArray();
		
		
		Elements horizon_pdInfoList=doc.getElementsByClass("horizon pdInfo");
		Elements data_tkInfoList=doc.getElementsByClass("data tkInfo");
		Elements horizon_tkAreaInfoInfoList=doc.getElementsByClass("horizon tkAreaInfo");
		
		//배송내역에 대한 json파싱
		for(Element element : data_tkInfoList)
		{
			Elements cellList=element.select("tbody>tr");

			JSONObject deliveryInfoJson=new JSONObject();
			for(Element row_tr:cellList)
			{
				deliveryInfoJson.put("date", row_tr.child(0).text());			// 날짜
				deliveryInfoJson.put("establishment",row_tr.child(1).text());	// 사업장
				deliveryInfoJson.put("deliverystate",row_tr.child(2).text());	// 배송상태
				deliveryInfoJson.put("deliverycontent",row_tr.child(3).text());	// 배송내용
				deliveryInfoJson.put("manager",row_tr.child(4).text());			// 담당직원
				deliveryInfoJson.put("underwriter",row_tr.child(5).text());		// 인수자
				deliveryInfoJson.put("office",row_tr.child(6).text());			// 영업소
				deliveryInfoJson.put("tel",row_tr.child(7).text());				// 연락처

			}
			deliveryInfoJsonArray.put(deliveryInfoJson);
		}
		
		// 물품정보에 대한 json파싱
		JSONObject contentInfoJson=new JSONObject();
		for(Element element : horizon_pdInfoList)
		{
			Elements cellList=element.select("tbody");
			
			contentInfoJson.put("invoice",cellList.get(0).child(0).child(1).text());			// 송장번호
			contentInfoJson.put("purchase",cellList.get(0).child(0).child(3).text());			// 상품명
			contentInfoJson.put("collectiondate",cellList.get(0).child(1).child(1).text());		// 집하일자
			contentInfoJson.put("deliveryspot",cellList.get(0).child(1).child(3).text());		// 배송지점
			contentInfoJson.put("collectionspot",cellList.get(0).child(2).child(1).text());		// 집하지점
			contentInfoJson.put("quantity",cellList.get(0).child(2).child(3).text());			// 수량
			contentInfoJson.put("sendname",cellList.get(0).child(3).child(1).text());			// 보내시는 분
			contentInfoJson.put("recievename",cellList.get(0).child(3).child(3).text());		// 받으시는 분
			contentInfoJson.put("address",cellList.get(0).child(4).child(1).text());			// 주소	
		}
		
		JSONObject storeInfoJson=new JSONObject();
		for(Element element:horizon_tkAreaInfoInfoList)
		{
			Elements cellList=element.select("tbody");
			
			storeInfoJson.put("lasttime", cellList.get(0).child(0).child(1).text());			// 최종처리시간
			storeInfoJson.put("expecttime", cellList.get(0).child(0).child(3).text());			// 배송예정시간
			storeInfoJson.put("laststore", cellList.get(0).child(1).child(1).text());			// 최종처리 사업장
			storeInfoJson.put("telephone1", cellList.get(0).child(1).child(3).text());			// 연락처1
			storeInfoJson.put("manager", cellList.get(0).child(2).child(1).text());				// 영업사원
			storeInfoJson.put("telephone2", cellList.get(0).child(2).child(3).text());			// 연락처2
		}
		
		JSONObject resultJson=new JSONObject();
		resultJson.put("deliveryInfo", deliveryInfoJsonArray);
		resultJson.put("contentInfo", contentInfoJson);
		resultJson.put("storeInfo", storeInfoJson);
		
		return resultJson.toString();
	}
	
	public void SearchHanjin(String invoiceNumber) throws IOException
	{
		
		/*
		 * {
			"serviceName":"hanjin",
			"invoiceNumber":"530633824744"
			}
		 */
		String baseURL="http://www.hanjin.co.kr";
		String path="";
		String reData="";
		String postData="";
		
		//Condition Check
		path="/kor/Main.do";
		_httpHandler.ClearHeader();
		_httpHandler.AddRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		_httpHandler.AddRequestHeader("Accept-Encoding","gzip, deflate");
		_httpHandler.AddRequestHeader("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
		_httpHandler.AddRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
		_httpHandler.AddRequestHeader("Connection","keep-alive");
		_httpHandler.AddRequestHeader("Host","www.hanjin.co.kr");
		
		_httpHandler.Send(baseURL+path);
		reData=_httpHandler.getResponseText();
		
		path=String.format("/kor/CMS/DeliveryMgr/WaybillResult.do?mCode=MN038&wblnum=%s&schLang=KR&wblnumText=", invoiceNumber);
		_httpHandler.Send(baseURL+path);
		reData=_httpHandler.getResponseText();
		
		//배송내역에 대한 json파싱
		
		doc=Jsoup.parse(reData);
		Elements board_list_tableInfoList=doc.getElementsByClass("board-list-table");
		Elements delivery_tblInfoList=doc.getElementsByClass("board-list-table delivery-tbl");
		
		for(Element element:delivery_tblInfoList)
		{
			System.out.println(element.select("td[data-label='상품명']").text());
			System.out.println(element.select("td[data-label='보내는 분']").text());
			System.out.println(element.select("td[data-label='받는 분']").text());
			System.out.println(element.select("td[data-label='받는 주소']").text());
		}
		
		for(Element element:board_list_tableInfoList)
		{
			Elements cellList=element.select("tbody>tr[class='']");
			for(Element cellElement : cellList)
			{
				System.out.println(cellElement.child(0).text());
				System.out.println(cellElement.child(1).text());
				System.out.println(cellElement.child(2).text());
				System.out.println(cellElement.child(3).text());
			}
		}
		
		System.out.println(reData);
	}
	
	public void SearchCJ(String invoiceNumber)
	{
		String baseURL="https://www.ilogen.com";
		String path="";
		String reData="";
		String postData="";
	}
	
	public void SearchLotte(String invoiceNumber)
	{
		String baseURL="https://www.ilogen.com";
		String path="";
		String reData="";
		String postData="";
	}
	
}
