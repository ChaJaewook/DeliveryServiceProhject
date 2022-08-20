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


// try catch로 오류처리하고 finally로 close필요

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
			case "rozen": //로젠택배
				result=SearchRozen(invoiceNumber);
				break;
			case "hanjin": //한진택배
				result=SearchHanjin(invoiceNumber);
				break;
			case "CJ": //CJ대한통운
				result=SearchCJ(invoiceNumber);
				break;
			case "Lotte": //롯데택배
				result=SearchLotte(invoiceNumber);
				break;
			case "PostOffice"://우체국 택배
				SearchPostOffice(invoiceNumber);
				break;
			default:
				break;
		}
		return result;
	}
	
	private String SearchRozen(String invoiceNumber) throws IOException 
	{

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
	
	private String SearchHanjin(String invoiceNumber) throws IOException
	{
		
		
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
		JSONObject resultJson=new JSONObject();
		
		doc=Jsoup.parse(reData);
		Elements board_list_tableInfoList=doc.getElementsByClass("board-list-table");
		Elements delivery_tblInfoList=doc.getElementsByClass("board-list-table delivery-tbl");
		Elements sognjang_numInfoList=doc.getElementsByClass("songjang-num");
		Elements delivery_timeInfoList=doc.getElementsByClass("delivery-time");
		
		//기본정보 파싱
		JSONObject senderInfoJson=new JSONObject();
		for(Element element:delivery_tblInfoList)
		{
			senderInfoJson.put("productname",element.select("td[data-label='상품명']").text());
			senderInfoJson.put("sender",element.select("td[data-label='상품명']").text());
			senderInfoJson.put("receiver",element.select("td[data-label='받는 분']").text());
			senderInfoJson.put("receiveraddress",element.select("td[data-label='받는 주소']").text());
		}
		
		
		//배송현황 파싱
		JSONArray deliveryInfoJsonArray=new JSONArray();
		for(Element element:board_list_tableInfoList)
		{
			
			Elements cellList=element.select("tbody>tr[class='']");
			if(cellList.isEmpty())
				continue;
			JSONObject deliveryInfoJson=new JSONObject();
			for(Element cellElement : cellList)
			{
				
				deliveryInfoJson.put("date", cellElement.child(0).text());// 날짜
				deliveryInfoJson.put("time", cellElement.child(1).text());// 시간
				deliveryInfoJson.put("productspot", cellElement.child(2).text());// 상품위치
				deliveryInfoJson.put("deliveryprogress", cellElement.child(3).text());// 배송 진행사항
			}
			deliveryInfoJsonArray.put(deliveryInfoJson);
		}
		
		//운송장 정보, 상태 
		JSONObject deliveryStateJson=new JSONObject();
		for(Element element:sognjang_numInfoList)
		{
			deliveryStateJson.put("invoice",element.select("span[class='num']").text());			//운송장번호
		}
		for(Element element:delivery_timeInfoList)
		{
			deliveryStateJson.put("date",element.select("span[class='date']").text());				//날짜
			deliveryStateJson.put("time",element.select("span[class='time']").text());				//시간
			deliveryStateJson.put("statecomment",element.select("p[class='comm-sec']").text()); 	//상태
		}
		
		resultJson.put("senderInfo", senderInfoJson);
		resultJson.put("deliveryInfo", deliveryInfoJsonArray);
		resultJson.put("deliverySate", deliveryStateJson);
		
		
		
		return resultJson.toString();
	}
	
	private String SearchCJ(String invoiceNumber) throws IOException
	{
		String baseURL="https://www.cjlogistics.com";
		String path="/ko/tool/parcel/tracking";
		String reData="";
		String postData="";
		String _csrf=""; 
				
		//Condition Check
		_httpHandler.ClearHeader();
		_httpHandler.AddRequestHeader("Host","www.cjlogistics.com");
		_httpHandler.AddRequestHeader("Connection","keep-alive");
		_httpHandler.AddRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Safari/537.36 Edg/104.0.1293.47");
		_httpHandler.AddRequestHeader("Connection","keep-alive");
		
		_httpHandler.Send(baseURL+path);
		reData=_httpHandler.getResponseText();
		
		System.out.println(reData);
		doc=Jsoup.parse(reData);
		_csrf=doc.select("input[name='_csrf']").attr("value");
		
		//배송내용 스크래핑
		//리턴값이 Json형태
		path="/ko/tool/parcel/tracking-detail";
		_httpHandler.ClearHeader();
		//_httpHandler.AddRequestHeader("cache","no-cache");
		_httpHandler.AddRequestHeader("Host","www.cjlogistics.com");
		_httpHandler.AddRequestHeader("Accept","application/json, text/javascript, */*; q=0.01");
		_httpHandler.AddRequestHeader("Accept-Encoding","gzip, deflate, br");
		_httpHandler.AddRequestHeader("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
		_httpHandler.AddRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
		_httpHandler.AddRequestHeader("X-Requested-With","XMLHttpRequest");
		_httpHandler.AddRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		//_httpHandler.AddRequestHeader("Content-Type","application/json;charset=UTF-8");
		
		_httpHandler.AddRequestHeader("Origin","https://www.cjlogistics.com");
		_httpHandler.AddRequestHeader("Connection","keep-alive");
		_httpHandler.AddRequestHeader("Referer","https://www.cjlogistics.com/ko/tool/parcel/tracking");
		postData="_csrf="+_csrf+"&";
		postData+="paramInvcNo="+invoiceNumber;
		
		_httpHandler.Send(baseURL+path,postData);
		reData=_httpHandler.getResponseText();
		
		return reData;
		
	}
	
	private String SearchLotte(String invoiceNumber) throws IOException
	{
		String baseURL="https://www.lotteglogis.com";
		String path="";
		String reData="";
		String postData="";
		
		_httpHandler.ClearHeader();
		_httpHandler.AddRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		_httpHandler.AddRequestHeader("Accept-Encoding","gzip, deflate, br");
		_httpHandler.AddRequestHeader("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
		_httpHandler.AddRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
		_httpHandler.AddRequestHeader("Referer","https://search.naver.com/search.naver?where=nexearch&sm=tab_jum&query=%EB%A1%AF%EB%8D%B0%ED%83%9D%EB%B0%B0");
		_httpHandler.AddRequestHeader("Connection","keep-alive");
		_httpHandler.AddRequestHeader("Host","www.lotteglogis.com");
		
		_httpHandler.Send(baseURL);
		reData=_httpHandler.getResponseText();
		

		_httpHandler.ClearHeader();
		_httpHandler.AddRequestHeader("Cache-Control","max-age=0");
		_httpHandler.AddRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		_httpHandler.AddRequestHeader("Accept-Encoding","gzip, deflate, br");
		_httpHandler.AddRequestHeader("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
		_httpHandler.AddRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
		_httpHandler.AddRequestHeader("Referer","https://www.lotteglogis.com/home/reservation/tracking/invoiceView");
		_httpHandler.AddRequestHeader("Connection","keep-alive");
		_httpHandler.AddRequestHeader("Host","www.lotteglogis.com");
		_httpHandler.AddRequestHeader("Content-Type","application/x-www-form-urlencoded");
		
		path="/home/reservation/tracking/linkView";
		postData="InvNo="+invoiceNumber;
		_httpHandler.Send(baseURL+path,postData);
		reData=_httpHandler.getResponseText();
		System.out.println(reData);
		
		doc=Jsoup.parse(reData);
		
		JSONObject deliveryState=new JSONObject();
		JSONArray deliveryInfoList=new JSONArray();
		JSONObject deliveryInfo=new JSONObject();
		JSONObject resultInfo=new JSONObject();
		
		Elements tblH_mt60=doc.getElementsByClass("tblH mt60");
		Elements tblH=doc.getElementsByClass("tblH").not("tblH mt60");
		
		/*for(Element element : tblH_mt60)
		{
			Elements cellList=element.select("tbody>tr>td");
			deliveryState.put("invoicenumber", cellList.get(0).text());
			deliveryState.put("sender", cellList.get(1).text());
			deliveryState.put("approach", cellList.get(2).text());
			deliveryState.put("deliveryresult", cellList.get(3).text());

		}*/
		int elementCount=0;
		for(Element element:tblH)
		{
			Elements cellList=element.select("tbody>tr");
			for(Element cell : cellList)
			{
				Elements dataList=cell.select("td");
				if(elementCount==0)
				{
					deliveryState.put("invoicenumber", dataList.get(0).text());		// 운송장 번호
					deliveryState.put("sender", dataList.get(1).text());			// 발송지
					deliveryState.put("approach", dataList.get(2).text());			// 도착지
					deliveryState.put("deliveryresult", dataList.get(3).text());	// 배달결과
					
					elementCount++;
				}
				else
				{
					deliveryInfo.put("stage", dataList.get(0).text());			// 단계
					deliveryInfo.put("time", dataList.get(1).text());			// 시간
					deliveryInfo.put("presentspot", dataList.get(2).text());	// 현재위치
					deliveryInfo.put("process", dataList.get(3).text());		// 처리현황
					
					deliveryInfoList.put(deliveryInfo);
				}
			
			}
		}
		
		resultInfo.put("deliveryState", deliveryState);
		resultInfo.put("deliveryInfo", deliveryInfoList);
		
		return resultInfo.toString();
		
	}
	

	private String SearchPostOffice(String invoiceNumber) throws IOException
	{
		String baseURL="https://service.epost.go.kr";
		String path="";
		String reData="";
		String postData="";
		
		//Condition Check
		path="/iservice/usr/trace/usrtrc001k01.jsp";
		_httpHandler.ClearHeader();
		_httpHandler.AddRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		_httpHandler.AddRequestHeader("Accept-Encoding","gzip, deflate, br");
		_httpHandler.AddRequestHeader("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
		_httpHandler.AddRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
		_httpHandler.AddRequestHeader("Connection","keep-alive");
		_httpHandler.AddRequestHeader("Host","service.epost.go.kr");
		
		_httpHandler.Send(baseURL+path);
		reData=_httpHandler.getResponseText();
		
		path="/trace.RetrieveDomRigiTraceList.comm";
		_httpHandler.ClearHeader();
		_httpHandler.AddRequestHeader("Cache-Control","max-age=0");
		_httpHandler.AddRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		_httpHandler.AddRequestHeader("Accept-Encoding","gzip, deflate, br");
		_httpHandler.AddRequestHeader("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
		_httpHandler.AddRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
		_httpHandler.AddRequestHeader("Content-Type","application/x-www-form-urlencoded");
		//_httpHandler.AddRequestHeader("Referer","https://service.epost.go.kr/iservice/usr/trace/usrtrc001k01.jsp");
		_httpHandler.AddRequestHeader("Origin","https://service.epost.go.kr");
		_httpHandler.AddRequestHeader("Connection","keep-alive");
		_httpHandler.AddRequestHeader("Host","service.epost.go.kr");
		postData=String.format("sid1=%s&displayHeader=", invoiceNumber);
		

		_httpHandler.Send(baseURL+path,postData);
		reData=_httpHandler.getResponseText();
		System.out.println(reData);
		return "";
		
	}
	
}
