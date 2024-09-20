package com.spacey.newsbuddy.buddy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.spacey.newsbuddy.android.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BuddyViewModel : ViewModel() {

    private val _responseText: MutableStateFlow<String> = MutableStateFlow("")
    val responseText: StateFlow<String> = _responseText

    private val model = GenerativeModel(
        "gemini-1.5-pro",
        // Retrieve API key as an environmental variable defined in a Build Configuration
        // see https://github.com/google/secrets-gradle-plugin for further instructions
        BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 1f
            topK = 64
            topP = 0.95f
            maxOutputTokens = 8192
            responseMimeType = "application/json"
        },
        // safetySettings = Adjust safety settings
        // See https://ai.google.dev/gemini-api/docs/safety-settings
        systemInstruction = content(role = "system") { text("I will share you a collection of news articles of todays news. Summarise those and create a conversation styled news curation. Give the text in seperate key in json and along with that, try to provide proper link to that article near the corresponding article's content. So when I ask more about that article, you need to summarise what's inside that particular article's link. Give the convo as a key and link in a key like { 'convo': \"Some long convo text\", 'link': That link here} So all the convo text can be combined by me to frame the final news summary.") },
    )

    fun promptTodaysNews() {
        // TODO: Remove file access
        val contentStream = model.generateContentStream(content { text(PROMPT) })
        viewModelScope.launch {
            contentStream.collect { response ->
                response.text?.let {
                    _responseText.value += it
                }
            }
        }
    }

    companion object {

        const val PROMPT = "{\n" +
                "  \"status\": \"ok\",\n" +
                "  \"totalResults\": 17,\n" +
                "  \"articles\": [\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"nbc-news\",\n" +
                "        \"name\": \"NBC News\"\n" +
                "      },\n" +
                "      \"author\": \"Sakshi Venkatraman\",\n" +
                "      \"title\": \"Harris targets Asian American voters with ad about her mother\",\n" +
                "      \"description\": \"Vice President Kamala Harris’ campaign highlights her mom, an Indian immigrant and cancer researcher who fought for civil rights.\",\n" +
                "      \"url\": \"https://www.nbcnews.com/news/asian-america/harris-targets-asian-american-voters-ad-mother-rcna171651\",\n" +
                "      \"urlToImage\": \"https://media-cldnry.s-nbcnews.com/image/upload/t_nbcnews-fp-1200-630,f_auto,q_auto:best/rockcms/2024-09/240918-Kamala-Harris-Shyamala-Harris-al-1119-0dfaa0.jpg\",\n" +
                "      \"publishedAt\": \"2024-09-18T16:32:20Z\",\n" +
                "      \"content\": \"Vice President Kamala Harris presidential campaign is making its latest push to Asian American voters, releasing a personal ad about her mother, Shyamala Gopalan. \\r\\nThe 60-second ad, titled My Mother… [+1466 chars]\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"google-news-in\",\n" +
                "        \"name\": \"Google News (India)\"\n" +
                "      },\n" +
                "      \"author\": \"The Hindu\",\n" +
                "      \"title\": \"Mpox in India: Second case of infection confirmed in UAE returnee in Kerala’s Malappuram - The Hindu\",\n" +
                "      \"description\": null,\n" +
                "      \"url\": \"https://news.google.com/rss/articles/CBMiugFBVV95cUxQOXBENDNSZ3UxZTVHYnNoYUY4dG11NjJORV9haTBXems4NmFqTU16SG95cmR3MXEzd1ZTVkJXWnVJNG5WQ1NYWUUzc0VUcjlaWVJnMkJocjBnVzMyeHVWZ0JzeFBBSURod0F2WFVuaE1fVVUtYW51RnhJNHBrNHhZMzVoVktnTTE5Z2JQMTZUQzBnZUlONW1mdmhfVE9wc09heDBuNTdaN0lwWldjMENQUFdxdjc4eWpPRFHSAcABQVVfeXFMTXJUMWJkbEtyOXRETEcwNWhhV20tUzdpN2xpUTIwdGhCWTZJeXBJWFFWZGlfY2w3VkZqWUZkZ1JNX2tWRlA5T2p1OHpBV1pwd0JRNkdTdWRmeFNyMnVtZDFBNGJzVkxGRDJQNmFELWg2T3hidWZTdWh1TjhwYnpoNDV2Mjc3YzdQaHFVWGNRTmxKZzZuU1I2dmNkZDZfZEFoS3U0c0dKejkwN0V4MGJsWXJ0U0N1N2hjUEVhMm0wMGRV?oc=5\",\n" +
                "      \"urlToImage\": null,\n" +
                "      \"publishedAt\": \"2024-09-18T14:44:00+00:00\",\n" +
                "      \"content\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"google-news-in\",\n" +
                "        \"name\": \"Google News (India)\"\n" +
                "      },\n" +
                "      \"author\": \"The Hindu\",\n" +
                "      \"title\": \"Indus Water Treaty: No more Indus Commission meetings till Treaty renegotiated, India tells Pakistan - The Hindu\",\n" +
                "      \"description\": null,\n" +
                "      \"url\": \"https://news.google.com/rss/articles/CBMixgFBVV95cUxOZTdOZTAzZkJjVU9pM0VXVV9XVjJubWpNRkdRWG5KbVBxNFpfZ1dUQWdIQ2xlNWNTMUplQ2QtUEtKdEN3QzducWUxM0Y4NnJoaXd5TFRqWTAydHNiYjU2TlhhU3RGY0JFeEw5bUNGVC1XRExxZ0ZFS2VSTy1Td3IxQ1JrS2N4OWdXY09sTGx6bjlfTGtBOEh2WFdzVVBmQUZRamdOTjNNbWRVdC1acWx4eXZPWE1NVjZhb2dRaTdIc3dxZGNBMUHSAcwBQVVfeXFMTVE4SFpjTHA2X2haVUtFU19zempJNnpMWC0tLXFkZlI1OHdyeTJNMnVQRzJJSDEteFNBOVpPQ3lWaFctQmdlU1YybEdHMlNWRVFweVYxcXFwU1RFUXpkMHlkSW9XWmZvOTNaOXRUdXlMY0FtM1NVa3pkWjFBd3ZBN1BDZUFCcVQyWkN3TVd6b19oN0ZKM1lOWnZXTGdOd3l3dnpDclhzOVB2WkNwQkhISEpmV05pUWI5VkxqRHViM0NkZUR2bjJQNmlrSGcy?oc=5\",\n" +
                "      \"urlToImage\": null,\n" +
                "      \"publishedAt\": \"2024-09-18T12:14:00+00:00\",\n" +
                "      \"content\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"bbc-news\",\n" +
                "        \"name\": \"BBC News\"\n" +
                "      },\n" +
                "      \"author\": \"BBC News\",\n" +
                "      \"title\": \"Ukraine war: Indians who fought in Russia return home with tales from war zone\",\n" +
                "      \"description\": \"Dozens of Indian men have been discharged from the Russian army and are making their way home.\",\n" +
                "      \"url\": \"https://www.bbc.co.uk/news/articles/cly6ve2x72xo\",\n" +
                "      \"urlToImage\": \"https://ichef.bbci.co.uk/news/1024/branded_news/dbc6/live/889f4a30-74e8-11ef-807a-672431f2b200.jpg\",\n" +
                "      \"publishedAt\": \"2024-09-18T08:52:13.1377922Z\",\n" +
                "      \"content\": \"\\\"It is a miracle I got back home,\\\" said Azad Yusuf Kumar, a resident of Indian-administered Kashmir, who was part of Mr Sufyan's group in the army. \\r\\n One minute you are digging a trench, and the nex… [+1439 chars]\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"google-news-in\",\n" +
                "        \"name\": \"Google News (India)\"\n" +
                "      },\n" +
                "      \"author\": \"The Times of India\",\n" +
                "      \"title\": \"Activist, adviser, minister & now CM: Delhi gets it youngest woman chief minister in Atishi - The Times of India\",\n" +
                "      \"description\": null,\n" +
                "      \"url\": \"https://news.google.com/rss/articles/CBMi8AFBVV95cUxPRTVHMnkzY2Znek1mS3ViQUtEMVlPQkdrUVRhdEVVeTlmYjlQa3ZTMjgwMnhRMWltbmlWbHVhNmdGYU9XMEtXZkNKZTZKTHVhVEhXQ0FDRFdkWk5rZXJJODkyY1Vhbjg5Mnc0OG1XTlhGSHZRM2JNX05DTzd2Z1l2aExXUGUwMEtvM2RtUzhUd09KRXlGZ3MwSDFuY25Jd1AwVk9WX3MwWkNaUnNmU3UwcFdKNTV3WHpBbDFqQ0RoVEVOZnNPVk1ib2VOX0FtQXZrUUF4b0h4OHpZbGZDanNfak9hMVN2YjBkRHRSQjZUWnLSAfYBQVVfeXFMTTBJak9RZW9JU2JrVHZ0T2txMGZ6Zklka0JCT1BuSU0yc29wcGV4T1d4MlV6OWN3emdldGNrOEwtQ3VsSXBsSjV3T2NVbHpiTlc0VEEybkl6eUs2WDkwelRyaXRYMmpUUUNqc21Od0RxanpBSXZsaXVNT1YxVzE5T2czVGNMd0lKSnBubnB4RFAwdWdQZ2dNRVdnWUk4QzFnQVJyWEMyVmg2Mm1qZnBraE5RbUlOZTZMRmphd2lXMlZGR0p3QXI4TDc0VTBQQTFKa2ZfMlpxSDFSWEZJekZLQjNPVnIyeFYzQ0pOaWVrRUZlTHlNSGNB?oc=5\",\n" +
                "      \"urlToImage\": null,\n" +
                "      \"publishedAt\": \"2024-09-18T02:03:00+00:00\",\n" +
                "      \"content\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"newsweek\",\n" +
                "        \"name\": \"Newsweek\"\n" +
                "      },\n" +
                "      \"author\": \"Dev Pragad, President and CEO\",\n" +
                "      \"title\": \"On the World Stage: Newsweek's Interviews with Indian Prime Minister Modi and Japanese Prime Minister Kishida\",\n" +
                "      \"description\": \"We aim to transcend surface-level narratives, offering coverage that creates opportunities for true international dialogue.\",\n" +
                "      \"url\": \"https://www.newsweek.com/world-stage-newsweeks-interviews-indian-prime-minister-modi-japanese-prime-minister-kishida-1895648\",\n" +
                "      \"urlToImage\": \"https://d.newsweek.com/en/full/2385807/dev-pragad-modi-kishida-newsweek-interview.jpg\",\n" +
                "      \"publishedAt\": \"2024-04-30T14:55:15Z\",\n" +
                "      \"content\": \"Newsweek's core mission is to provide our readers with a deep and nuanced understanding of the world around them. That means embracing a truly global perspective. We aim to transcend surface-level na… [+5470 chars]\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"fortune\",\n" +
                "        \"name\": \"Fortune\"\n" +
                "      },\n" +
                "      \"author\": \"Emma Hinchliffe, Paige McGlauflin\",\n" +
                "      \"title\": \"Why a former SoftBank partner is tackling mid-career drop-off for working mothers\",\n" +
                "      \"description\": \"Former SoftBank partner and Facebook India director Kirthiga Reddy is the cofounder of Laddrr, a resource hub for working mothers aiming to prevent mid-career drop-off.\",\n" +
                "      \"url\": \"https://fortune.com/2022/06/01/former-softbank-partner-tackling-mid-career-drop-off-for-working-mothers/\",\n" +
                "      \"urlToImage\": \"https://content.fortune.com/wp-content/uploads/2022/05/Kirthiga1.jpg?resize=1200,600\",\n" +
                "      \"publishedAt\": \"2022-06-01T13:22:34Z\",\n" +
                "      \"content\": \"Skip to Content\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"the-hindu\",\n" +
                "        \"name\": \"The Hindu\"\n" +
                "      },\n" +
                "      \"author\": \"Ananth Krishnan\",\n" +
                "      \"title\": \"Dalai Lama’s close aides targeted on Pegasus spyware list\",\n" +
                "      \"description\": \"‘Analysis indicates that the Indian govt. was selecting the potential targets’\",\n" +
                "      \"url\": \"https://www.thehindu.com/news/international/dalai-lamas-close-aides-targeted-on-pegasus-spyware-list/article35474285.ece\",\n" +
                "      \"urlToImage\": \"https://www.thehindu.com/news/international/dvshb0/article35474284.ece/ALTERNATES/LANDSCAPE_615/thjc-DalaiLama\",\n" +
                "      \"publishedAt\": \"2021-07-22T15:47:01Z\",\n" +
                "      \"content\": \"Several of the top India-based aides to the Tibetan spiritual leader, the Dalai Lama, figure on the list of potential targets for spying using the Pegasus spyware, according to a report on Thursday. … [+2399 chars]\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"rte\",\n" +
                "        \"name\": \"RTE\"\n" +
                "      },\n" +
                "      \"author\": \"RTÉ News\",\n" +
                "      \"title\": \"UK continues with reopening plan despite concerns\",\n" +
                "      \"description\": \"British ministers are pushing on with a major easing of restrictions on Monday despite concerns over the Indian variant of coronavirus, as they were criticised for allowing the strain's import.\",\n" +
                "      \"url\": \"https://www.rte.ie/news/uk/2021/0515/1221764-uk-indian-variant/\",\n" +
                "      \"urlToImage\": \"https://img.rasset.ie/0016cb40-1600.jpg\",\n" +
                "      \"publishedAt\": \"2021-05-15T09:33:48Z\",\n" +
                "      \"content\": \"British ministers are pushing on with a major easing of restrictions on Monday despite concerns over the Indian variant of coronavirus, as they were criticised for allowing the strain's import.\\r\\nPrim… [+5066 chars]\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"the-times-of-india\",\n" +
                "        \"name\": \"The Times of India\"\n" +
                "      },\n" +
                "      \"author\": \"Rajat Pandit\",\n" +
                "      \"title\": \"Army explores procurement of 350 light tanks for mountainous terrain after border standoff with China\",\n" +
                "      \"description\": \"India News:  The Army is now exploring the possibility of procuring 350 light tanks, which can also be transported by air, to augment its firepower in high-altitu\",\n" +
                "      \"url\": \"http://timesofindia.indiatimes.com/india/army-explores-procurement-of-350-light-tanks-for-mountainous-terrain-after-border-standoff-with-china/articleshow/82217825.cms\",\n" +
                "      \"urlToImage\": \"https://static.toiimg.com/thumb/msid-82217908,width-1070,height-580,imgsize-264639,resizemode-75,overlay-toi_sw,pt-32,y_pad-40/photo.jpg\",\n" +
                "      \"publishedAt\": \"2021-04-23T08:29:00Z\",\n" +
                "      \"content\": \"Army explores procurement of 350 light tanks for mountainous terrain after border standoff with China\\r\\n<ul><li>News</li>\\r\\n<li>India News</li>\\r\\n<li>Army explores procurement of 350 light tanks for mou… [+58 chars]\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"the-times-of-india\",\n" +
                "        \"name\": \"The Times of India\"\n" +
                "      },\n" +
                "      \"author\": \"Times Of India\",\n" +
                "      \"title\": \"PBKS vs MI Live Score, IPL 2021: Mumbai Indians seek consistency; Punjab Kings eye return to winning ways\",\n" +
                "      \"description\": \"IPL Live Score: Mumbai Indians seek consistency; Punjab Kings eye return to winning ways. Stay with TOI to get IPL live score, playing 11, scorecard, highlights and ball by ball score updates of the 17th IPL match between Punjab Kings and Mumbai Indians.\",\n" +
                "      \"url\": \"http://timesofindia.indiatimes.com/sports/cricket/ipl/live-blog/punjab-kings-vs-mumbai-indians-pbks-vs-mi-live-score-ipl-2021-17th-match-chennai/liveblog/82214950.cms\",\n" +
                "      \"urlToImage\": \"https://static.toiimg.com/thumb/msid-82214950,width-1070,height-580,imgsize-157009,resizemode-75,overlay-toi_sw,pt-32,y_pad-40/photo.jpg\",\n" +
                "      \"publishedAt\": \"2021-04-23T05:44:49Z\",\n" +
                "      \"content\": \"Ravi Bishnoi return on the cards?\\r\\nDeepak Hooda showed what he is capable of, but greater consistency would be needed from him if the team has to prosper. They bet on Australian pace imports Jhye Ric… [+3131 chars]\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"the-times-of-india\",\n" +
                "        \"name\": \"The Times of India\"\n" +
                "      },\n" +
                "      \"author\": \"PTI\",\n" +
                "      \"title\": \"Zydus Cadila gets DCGI nod for hepatitis drug for Covid-19 treatment\",\n" +
                "      \"description\": \"India News: Drug firm Zydus Cadila on Friday said it has received restricted emergency use approval from the Indian drug regulator for the use of Pegylated Interf\",\n" +
                "      \"url\": \"http://timesofindia.indiatimes.com/india/zydus-cadila-gets-dcgi-nod-for-hepatitis-drug-for-covid-19-treatment/articleshow/82214909.cms\",\n" +
                "      \"urlToImage\": \"https://static.toiimg.com/thumb/msid-82214921,width-1070,height-580,imgsize-98052,resizemode-75,overlay-toi_sw,pt-32,y_pad-40/photo.jpg\",\n" +
                "      \"publishedAt\": \"2021-04-23T05:43:00Z\",\n" +
                "      \"content\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"the-times-of-india\",\n" +
                "        \"name\": \"The Times of India\"\n" +
                "      },\n" +
                "      \"author\": \"Bloomberg\",\n" +
                "      \"title\": \"Even record death toll may hide extent of India’s Covid crisis\",\n" +
                "      \"description\": \"India News: Bodies piling up at crematoriums and burial grounds across India are sparking concerns that the death toll from a ferocious new Covid-19 wave may be m.\",\n" +
                "      \"url\": \"http://timesofindia.indiatimes.com/india/even-record-death-toll-may-hide-extent-of-indias-covid-crisis/articleshow/82213444.cms\",\n" +
                "      \"urlToImage\": \"https://static.toiimg.com/thumb/msid-82213819,width-1070,height-580,imgsize-232887,resizemode-75,overlay-toi_sw,pt-32,y_pad-40/photo.jpg\",\n" +
                "      \"publishedAt\": \"2021-04-23T04:41:00Z\",\n" +
                "      \"content\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"the-times-of-india\",\n" +
                "        \"name\": \"The Times of India\"\n" +
                "      },\n" +
                "      \"author\": \"Dipak K Dash\",\n" +
                "      \"title\": \"Government to provide 5 kg free food grains to poor for May & June\",\n" +
                "      \"description\": \"India News: The government on Friday announced to provide 5 kg free food grains to the poor for May and June 2021. This will cover nearly 80 crore beneficiaries u\",\n" +
                "      \"url\": \"http://timesofindia.indiatimes.com/india/government-to-provide-5-kg-free-food-grains-to-poor-for-may-june/articleshow/82213582.cms\",\n" +
                "      \"urlToImage\": \"https://static.toiimg.com/thumb/msid-82213583,width-1070,height-580,imgsize-1921513,resizemode-75,overlay-toi_sw,pt-32,y_pad-40/photo.jpg\",\n" +
                "      \"publishedAt\": \"2021-04-23T04:23:00Z\",\n" +
                "      \"content\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"the-times-of-india\",\n" +
                "        \"name\": \"The Times of India\"\n" +
                "      },\n" +
                "      \"author\": \"TIMESOFINDIA.COM\",\n" +
                "      \"title\": \"'Inappropriate': PM Modi objects to 'protocol break' during meeting; Delhi CM expresses regret\",\n" +
                "      \"description\": \"India News: Delhi chief minister Arvind Kejriwal on Friday faced flak for sharing a live telecast of an \\\"in-house\\\" meeting with Prime Minister Narendra Modi where\",\n" +
                "      \"url\": \"http://timesofindia.indiatimes.com/india/delhi-cmo-expresses-regret-over-televised-address-during-meeting-with-pm-modi/articleshow/82213159.cms\",\n" +
                "      \"urlToImage\": \"https://static.toiimg.com/thumb/msid-82213159,width-1070,height-580,imgsize-134672,resizemode-75,overlay-toi_sw,pt-32,y_pad-40/photo.jpg\",\n" +
                "      \"publishedAt\": \"2021-04-23T03:54:00Z\",\n" +
                "      \"content\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"the-times-of-india\",\n" +
                "        \"name\": \"The Times of India\"\n" +
                "      },\n" +
                "      \"author\": \"Times Of India\",\n" +
                "      \"title\": \"Coronavirus in India live updates: Zydus' Virafin gets DCGI nod for Covid treatment\",\n" +
                "      \"description\": \"In yet another grim milestone, India recorded 3.3 lakh new Covid-19 cases, and 2,263 deaths in a day. Meanwhile, active cases crossed the 24-lakh mar\",\n" +
                "      \"url\": \"http://timesofindia.indiatimes.com/india/coronavirus-in-india-covid-19-vaccine-cases-lockdown-live-updates-23-april-2021/liveblog/82205841.cms\",\n" +
                "      \"urlToImage\": \"https://static.toiimg.com/thumb/msid-82205841,width-1070,height-580,imgsize-148788,resizemode-75,overlay-toi_sw,pt-32,y_pad-40/photo.jpg\",\n" +
                "      \"publishedAt\": \"2021-04-22T16:20:06Z\",\n" +
                "      \"content\": \"Zydus Cadila gets DCGI nod for hepatitis drug for Covid-19 treatment\\r\\nMake Covid-19 vaccination affordable, accessible through Jan Aushadi scheme: IMA\\r\\nThe IMA has demanded that the Covid-19 vaccine … [+4487 chars]\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"source\": {\n" +
                "        \"id\": \"espn-cric-info\",\n" +
                "        \"name\": \"ESPN Cric Info\"\n" +
                "      },\n" +
                "      \"author\": null,\n" +
                "      \"title\": \"I'm fighting my own benchmarks - R Ashwin | ESPNcricinfo.com\",\n" +
                "      \"description\": \"India's No. 1 offspinner talks to Manjrekar on his form abroad, injuries and more | ESPNcricinfo.com\",\n" +
                "      \"url\": \"http://www.espncricinfo.com/story/_/id/29102228/fighting-my-own-benchmarks-r-ashwin\",\n" +
                "      \"urlToImage\": \"https://a4.espncdn.com/combiner/i?img=%2Fi%2Fcricket%2Fcricinfo%2F1219773_1296x729.jpg\",\n" +
                "      \"publishedAt\": \"2020-04-25T03:00:09Z\",\n" +
                "      \"content\": \"R Ashwin has said that he is \\\"fighting my own benchmarks\\\" because his Test performances overseas are being measured against his heroics in India. Despite being the country's best long-form spinner in… [+3347 chars]\"\n" +
                "    }\n" +
                "  ]\n" +
                "}"
    }
}

