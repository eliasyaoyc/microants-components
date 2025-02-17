import org.junit.Test;
import yyc.open.component.report.commons.http.HttpKit;
import yyc.open.component.report.commons.http.Result;

/**
 * @author Elias (siran0611@gmail.com)
 */
public class MultiPieTest {

	@Test
	public void multiPie() {
		String multiPieJson = "{\n"
				+ "    \"reqMethod\":\"echarts\",\n"
				+ "    \"file\":\"/Users/eliasyao/Desktop/ryfh230BAR62IMeQX7EQ.png\",\n"
				+ "    \"opt\":{\n"
				+ "    \"tooltip\":{\n"
				+ "        \"trigger\":\"item\",\n"
				+ "        \"formatter\":\"{a} &lt;br/&gt;{b}: {c} ({d}%)\"\n"
				+ "    },\n"
				+ "    \"legend\":{\n"
				+ "        \"data\":[\n"
				+ "            \"Direct\",\n"
				+ "            \"Marketing\",\n"
				+ "            \"Search Engine\",\n"
				+ "            \"Email\",\n"
				+ "            \"Union Ads\",\n"
				+ "            \"Video Ads\",\n"
				+ "            \"Baidu\",\n"
				+ "            \"Google\",\n"
				+ "            \"Bing\",\n"
				+ "            \"Others\"\n"
				+ "        ]\n"
				+ "    },\n"
				+ "    \"series\":[\n"
				+ "        {\n"
				+ "            \"name\":\"Access From\",\n"
				+ "            \"type\":\"pie\",\n"
				+ "            \"selectedMode\":\"single\",\n"
				+ "            \"radius\":[\n"
				+ "                \"0\",\n"
				+ "                \"30%\"\n"
				+ "            ],\n"
				+ "            \"label\":{\n"
				+ "                \"position\":\"inner\",\n"
				+ "                \"fontSize\":14\n"
				+ "            },\n"
				+ "            \"labelLine\":{\n"
				+ "                \"show\":false\n"
				+ "            },\n"
				+ "            \"data\":[\n"
				+ "                {\n"
				+ "                    \"value\":1548,\n"
				+ "                    \"name\":\"Search Engine\"\n"
				+ "                },\n"
				+ "                {\n"
				+ "                    \"value\":775,\n"
				+ "                    \"name\":\"Direct\"\n"
				+ "                },\n"
				+ "                {\n"
				+ "                    \"value\":679,\n"
				+ "                    \"name\":\"Marketing', selected: true\"\n"
				+ "                }\n"
				+ "            ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "            \"name\":\"Access From\",\n"
				+ "            \"type\":\"pie\",\n"
				+ "            \"radius\":[\n"
				+ "                \"45%\",\n"
				+ "                \"60%\"\n"
				+ "            ],\n"
				+ "            \"labelLine\":{\n"
				+ "                \"length\":30\n"
				+ "            },\n"
				+ "            \"label\":{\n"
				+ "                \"formatter\":\"{a|{a}}{abg|}\\n{hr|}\\n  {b|{b}：}{c}  {per|{d}%}  \",\n"
				+ "                \"backgroundColor\":\"#F6F8FC\",\n"
				+ "                \"borderColor\":\"#8C8D8E\",\n"
				+ "                \"borderWidth\":1,\n"
				+ "                \"borderRadius\":4,\n"
				+ "                \"rich\":{\n"
				+ "                    \"a\":{\n"
				+ "                        \"color\":\"#6E7079\",\n"
				+ "                        \"lineHeight\":22,\n"
				+ "                        \"align\":\"center\"\n"
				+ "                    },\n"
				+ "                    \"hr\":{\n"
				+ "                        \"borderColor\":\"#8C8D8E\",\n"
				+ "                        \"width\":\"100%\",\n"
				+ "                        \"borderWidth\":1,\n"
				+ "                        \"height\":0\n"
				+ "                    },\n"
				+ "                    \"b\":{\n"
				+ "                        \"color\":\"#4C5058\",\n"
				+ "                        \"fontSize\":14,\n"
				+ "                        \"fontWeight\":\"bold\",\n"
				+ "                        \"lineHeight\":33\n"
				+ "                    },\n"
				+ "                    \"per\":{\n"
				+ "                        \"color\":\"#fff\",\n"
				+ "                        \"backgroundColor\":\"#4C5058\",\n"
				+ "                        \"padding\":[\n"
				+ "                            3,\n"
				+ "                            4\n"
				+ "                        ],\n"
				+ "                        \"borderRadius\":4\n"
				+ "                    }\n"
				+ "                }\n"
				+ "            },\n"
				+ "            \"data\":[\n"
				+ "                {\n"
				+ "                    \"value\":1048,\n"
				+ "                    \"name\":\"Baidu\"\n"
				+ "                },\n"
				+ "                {\n"
				+ "                    \"value\":335,\n"
				+ "                    \"name\":\"Direct\"\n"
				+ "                },\n"
				+ "                {\n"
				+ "                    \"value\":310,\n"
				+ "                    \"name\":\"Email\"\n"
				+ "                },\n"
				+ "                {\n"
				+ "                    \"value\":251,\n"
				+ "                    \"name\":\"Google\"\n"
				+ "                },\n"
				+ "                {\n"
				+ "                    \"value\":234,\n"
				+ "                    \"name\":\"Union Ads\"\n"
				+ "                },\n"
				+ "                {\n"
				+ "                    \"value\":147,\n"
				+ "                    \"name\":\"Bing\"\n"
				+ "                },\n"
				+ "                {\n"
				+ "                    \"value\":135,\n"
				+ "                    \"name\":\"Video Ads\"\n"
				+ "                },\n"
				+ "                {\n"
				+ "                    \"value\":102,\n"
				+ "                    \"name\":\"Others\"\n"
				+ "                }\n"
				+ "            ]\n"
				+ "        }\n"
				+ "    ]\n"
				+ "}\n"
				+ "}";
		Result result = HttpKit.builder()
				.post("http://127.0.0.1:6666")
				.body(multiPieJson)
				.build().get();
		System.out.println(result);
	}
}