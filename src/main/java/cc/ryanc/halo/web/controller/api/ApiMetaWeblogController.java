package cc.ryanc.halo.web.controller.api;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.ryanc.halo.service.UserService;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Validator;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * MetaWeblog API requests processing.
 * <p>
 * Implemented the following APIs:
 * <ul>
 * <li>blogger.deletePost</li>
 * <li>blogger.getUsersBlogs</li>
 * <li>metaWeblog.editPost</li>
 * <li>metaWeblog.getCategories</li>
 * <li>metaWeblog.getPost</li>
 * <li>metaWeblog.getRecentPosts</li>
 * <li>metaWeblog.newPost</li>
 * </ul>
 * </p>
 * 
 * @author : 这块显卡有点冷
 * @date : 2018/10/10
 */
@RestController
@RequestMapping(value = "/api/metaweblog")
public class ApiMetaWeblogController {
	
    /**
     * Logger.
     */
	private static final Logger log = LoggerFactory.getLogger(ApiMetaWeblogController.class);
	
	 /**
     * Key of method call.
     */
    private static final String METHOD_CALL = "methodCall";

    /**
     * Key of method name.
     */
    private static final String METHOD_NAME = "methodName";

    /**
     * Method name: "blogger.getUsersBlogs".
     */
    private static final String METHOD_GET_USERS_BLOGS = "blogger.getUsersBlogs";

    /**
     * Method name: "metaWeblog.getCategories".
     */
    private static final String METHOD_GET_CATEGORIES = "metaWeblog.getCategories";

    /**
     * Method name: "metaWeblog.getRecentPosts".
     */
    private static final String METHOD_GET_RECENT_POSTS = "metaWeblog.getRecentPosts";

    /**
     * Method name: "metaWeblog.newPost".
     */
    private static final String METHOD_NEW_POST = "metaWeblog.newPost";

    /**
     * Method name: "metaWeblog.editPost".
     */
    private static final String METHOD_EDIT_POST = "metaWeblog.editPost";

    /**
     * Method name: "metaWeblog.getPost".
     */
    private static final String METHOD_GET_POST = "metaWeblog.getPost";

    /**
     * Method name: "blogger.deletePost".
     */
    private static final String METHOD_DELETE_POST = "blogger.deletePost";

    /**
     * Argument "username" index.
     */
    private static final int INDEX_USER_EMAIL = 1;

    /**
     * Argument "postid" index.
     */
    private static final int INDEX_POST_ID = 0;

    /**
     * Argument "password" index.
     */
    private static final int INDEX_USER_PWD = 2;

    /**
     * Argument "numberOfPosts" index.
     */
    private static final int INDEX_NUM_OF_POSTS = 3;

    /**
     * Argument "post" index.
     */
    private static final int INDEX_POST = 3;

    /**
     * Argument "publish" index.
     */
    private static final int INDEX_PUBLISH = 4;
    
    @Autowired
    private UserService userService;

    /**
     * MetaWeblog requests processing.
     *
     */
    @PostMapping
    public void metaWeblog(HttpServletRequest request) {
    	
//        final TextXMLRenderer renderer = new TextXMLRenderer();
//        context.setRenderer(renderer);

        String responseContent;
    	try {
    		final ServletInputStream inputStream = request.getInputStream();
    		final String xml = Convert.toStr(inputStream);
    		final JSONObject requestJSONObject = JSONUtil.parseFromXml(xml);
    		
    		final JSONObject methodCall = requestJSONObject.getJSONObject(METHOD_CALL);
    		final String methodName = methodCall.getStr(METHOD_NAME);
    		
    		log.info("MetaWeblog[methodName={0}]", methodName);
    		
    		final JSONArray params = methodCall.getJSONObject("params").getJSONArray("param");
    		
    		if (METHOD_DELETE_POST.equals(methodName)) {
                params.remove(0); // Removes the first argument "appkey"
    		}
    		// 用户名 or 邮箱
    		final String userLoginName = params.getJSONObject(INDEX_USER_EMAIL).getJSONObject("value").getStr("string");
    		final String userPwd = params.getJSONObject(INDEX_USER_PWD).getJSONObject("value").getStr("string");
    		final JSONObject user;
    		
    		if (Validator.isEmail(userLoginName)) {
                user = JSONUtil.parseObj(userService.userLoginByEmail(userLoginName, SecureUtil.md5(userPwd)));
            } else {
                user = JSONUtil.parseObj(userService.userLoginByName(userLoginName, SecureUtil.md5(userPwd)));
            }
    		
            if (null == user) {
                throw new Exception("No user [login name =" + userLoginName + "]");
            }
            final String userId = user.getStr("Keys.OBJECT_ID");
            
            if (METHOD_GET_USERS_BLOGS.equals(methodName)) {
                responseContent = getUsersBlogs();
            } else if (METHOD_GET_CATEGORIES.equals(methodName)) {
                responseContent = getCategories();
            } else if (METHOD_GET_RECENT_POSTS.equals(methodName)) {
                final int numOfPosts = params.getJSONObject(INDEX_NUM_OF_POSTS).getJSONObject("value").getInt("int");
                responseContent = getRecentPosts(numOfPosts);
            } else if (METHOD_NEW_POST.equals(methodName)) {
                final JSONObject article = parsetPost(methodCall);
//                article.put(Article.ARTICLE_AUTHOR_ID, userId);
//                addArticle(article);

//                final StringBuilder stringBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodResponse>").append("<params><param><value><string>").append(article.getString(Keys.OBJECT_ID)).append("</string></value></param></params></methodResponse>");
//                responseContent = stringBuilder.toString();
            } else if (METHOD_GET_POST.equals(methodName)) {
                final String postId = params.getJSONObject(INDEX_POST_ID).getJSONObject("value").getStr("string");
                responseContent = getPost(postId);
            } else if (METHOD_EDIT_POST.equals(methodName)) {
//                final JSONObject article = parsetPost(methodCall);
//                final String postId = params.getJSONObject(INDEX_POST_ID).getJSONObject("value").getString("string");
//                article.put(Keys.OBJECT_ID, postId);
//                article.put(Article.ARTICLE_AUTHOR_ID, userId);
//                final JSONObject updateArticleRequest = new JSONObject();
//                updateArticleRequest.put(Article.ARTICLE, article);
//                articleMgmtService.updateArticle(updateArticleRequest);
//
//                final StringBuilder stringBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodResponse>").append("<params><param><value><string>").append(postId).append(
//                        "</string></value></param></params></methodResponse>");
//                responseContent = stringBuilder.toString();
            } else if (METHOD_DELETE_POST.equals(methodName)) {
                final String postId = params.getJSONObject(INDEX_POST_ID).getJSONObject("value").getStr("string");
//                articleMgmtService.removeArticle(postId);
//
//                final StringBuilder stringBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodResponse>").append("<params><param><value><boolean>").append(true).append(
//                        "</boolean></value></param></params></methodResponse>");
//                responseContent = stringBuilder.toString();
            } else {
                throw new UnsupportedOperationException("Unsupported method[name=" + methodName + "]");
            }
            
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    
    /**
     * Processes {@value #METHOD_GET_POST}.
     *
     * @param postId the specified post id
     * @return method response XML
     * @throws Exception exception
     */
    private String getPost(final String postId) throws Exception {
        final StringBuilder stringBuilder = new StringBuilder(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodResponse><params><param><value>");
        final String posts = buildPost(postId);
        stringBuilder.append(posts);
        stringBuilder.append("</value></param></params></methodResponse>");

        return stringBuilder.toString();
    }
    

    /**
     * Adds the specified article.
     *
     * @param article the specified article
     * @throws Exception exception
     */
    private void addArticle(final JSONObject article) throws Exception {
//        final Transaction transaction = articleRepository.beginTransaction();
//        try {
//            articleMgmtService.addArticleInternal(article);
//            transaction.commit();
//        } catch (final ServiceException e) {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//
//            throw e;
//        }
    }

    /**
     * Parses the specified method call for an article.
     *
     * @param methodCall the specified method call
     * @return article
     * @throws Exception exception
     */
    private JSONObject parsetPost(final JSONObject methodCall) throws Exception {
        final JSONObject ret = new JSONObject();

//        final JSONArray params = methodCall.getJSONObject("params").getJSONArray("param");
//        final JSONObject post = params.getJSONObject(INDEX_POST).getJSONObject("value").getJSONObject("struct");
//        final JSONArray members = post.getJSONArray("member");
//        for (int i = 0; i < members.length(); i++) {
//            final JSONObject member = members.getJSONObject(i);
//            final String name = member.getString("name");
//            if ("dateCreated".equals(name)) {
//                final String dateString = member.getJSONObject("value").getString("dateTime.iso8601");
//                Date date;
//
//                try {
//                    date = (Date) DateFormatUtils.ISO_DATETIME_FORMAT.parseObject(dateString);
//                } catch (final ParseException e) {
//                    LOGGER.log(Level.DEBUG,
//                            "Parses article create date failed with ISO8601, retry to parse with "
//                                    + "pattern[yyyy-MM-dd'T'HH:mm:ss, yyyyMMdd'T'HH:mm:ss'Z']");
//                    date = DateUtils.parseDate(dateString, new String[]{"yyyyMMdd'T'HH:mm:ss", "yyyyMMdd'T'HH:mm:ss'Z'"});
//                }
//                ret.put(Article.ARTICLE_CREATED, date.getTime());
//            } else if ("title".equals(name)) {
//                ret.put(Article.ARTICLE_TITLE, member.getJSONObject("value").getString("string"));
//            } else if ("description".equals(name)) {
//                final String content = member.getJSONObject("value").getString("string");
//                ret.put(Article.ARTICLE_CONTENT, content);
//                ret.put(Article.ARTICLE_ABSTRACT, Article.getAbstract(Jsoup.parse(content).text()));
//            } else if ("categories".equals(name)) {
//                final StringBuilder tagBuilder = new StringBuilder();
//                final JSONObject data = member.getJSONObject("value").getJSONObject("array").getJSONObject("data");
//                if (0 == data.length()) {
//                    throw new Exception("At least one Tag");
//                }
//
//                final Object value = data.get("value");
//                if (value instanceof JSONArray) {
//                    final JSONArray tags = (JSONArray) value;
//
//                    for (int j = 0; j < tags.length(); j++) {
//                        final String tagTitle = tags.getJSONObject(j).getString("string");
//
//                        tagBuilder.append(tagTitle);
//
//                        if (j < tags.length() - 1) {
//                            tagBuilder.append(",");
//                        }
//                    }
//                } else {
//                    final JSONObject tag = (JSONObject) value;
//                    tagBuilder.append(tag.getString("string"));
//                }
//
//                ret.put(Article.ARTICLE_TAGS_REF, tagBuilder.toString());
//            }
//        }
//
//        final boolean publish = 1 == params.getJSONObject(INDEX_PUBLISH).getJSONObject("value").getInt("boolean");
//        ret.put(Article.ARTICLE_IS_PUBLISHED, publish);
//        ret.put(Article.ARTICLE_COMMENTABLE, true);
//        ret.put(Article.ARTICLE_VIEW_PWD, "");

        return ret;
    }

    /**
     * Processes {@value #METHOD_GET_RECENT_POSTS}.
     *
     * @param fetchSize the specified fetch size
     * @return method response XML
     * @throws Exception exception
     */
    private String getRecentPosts(final int fetchSize) throws Exception {
        final StringBuilder stringBuilder = new StringBuilder(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodResponse><params><param><value><array><data>");
//        final String posts = buildRecentPosts(fetchSize);
//        stringBuilder.append(posts);
//        stringBuilder.append("</data></array></value></param></params></methodResponse>");

        return stringBuilder.toString();
    }

    /**
     * Processes {@value #METHOD_GET_CATEGORIES}.
     *
     * @return method response XML
     * @throws Exception exception
     */
    private String getCategories() throws Exception {
        final StringBuilder stringBuilder = new StringBuilder(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodResponse><params><param><value><array><data>");
//        final String categories = buildCategories();
//        stringBuilder.append(categories);
//        stringBuilder.append("</data></array></value></param></params></methodResponse>");

        return stringBuilder.toString();
    }

    /**
     * Processes {@value #METHOD_GET_USERS_BLOGS}.
     *
     * @return method response XML
     * @throws Exception exception
     */
    private String getUsersBlogs() throws Exception {
        final StringBuilder stringBuilder = new StringBuilder(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodResponse><params><param><value><array><data><value><struct>");
//        final JSONObject preference = preferenceQueryService.getPreference();
//        final String blogInfo = buildBlogInfo(preference);
//        stringBuilder.append(blogInfo);
//        stringBuilder.append("</struct></value></data></array></value></param></params></methodResponse>");

        return stringBuilder.toString();
}
    

    /**
     * Builds a post (post struct) with the specified post id.
     *
     * @param postId the specified post id
     * @return blog info XML
     * @throws Exception exception
     */
    private String buildPost(final String postId) throws Exception {
        final StringBuilder stringBuilder = new StringBuilder();

//        final JSONObject result = articleQueryService.getArticle(postId);
//        if (null == result) {
//            throw new Exception("Not found article[id=" + postId + "]");
//        }
//
//        final JSONObject article = result.getJSONObject(Article.ARTICLE);
//        final long createDate = article.getLong(Article.ARTICLE_CREATED);
//        final String articleTitle = StringEscapeUtils.escapeXml(article.getString(Article.ARTICLE_TITLE));
//
//        stringBuilder.append("<struct>");
//        stringBuilder.append("<member><name>dateCreated</name>").append("<value><dateTime.iso8601>").append(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(createDate)).append(
//                "</dateTime.iso8601></value></member>");
//        stringBuilder.append("<member><name>description</name>").append("<value>").append(StringEscapeUtils.escapeXml(article.getString(Article.ARTICLE_CONTENT))).append(
//                "</value></member>");
//        stringBuilder.append("<member><name>title</name>").append("<value>").append(articleTitle).append("</value></member>");
//        stringBuilder.append("<member><name>categories</name>").append("<value><array><data>");
//        final JSONArray tags = article.getJSONArray(Article.ARTICLE_TAGS_REF);
//
//        for (int i = 0; i < tags.length(); i++) {
//            final String tagTitle = tags.getJSONObject(i).getString(Tag.TAG_TITLE);
//            stringBuilder.append("<value>").append(tagTitle).append("</value>");
//        }
//        stringBuilder.append("</data></array></value></member></struct>");

        return stringBuilder.toString();
    }
    
    /**
     * Builds recent posts (array of post structs) with the specified fetch size.
     *
     * @param fetchSize the specified fetch size
     * @return blog info XML
     * @throws Exception exception
     */
    private String buildRecentPosts(final int fetchSize) throws Exception {
        final StringBuilder stringBuilder = new StringBuilder();
//        final List<JSONObject> recentArticles = articleQueryService.getRecentArticles(fetchSize);
//        for (final JSONObject article : recentArticles) {
//            final long createDate = article.getLong(Article.ARTICLE_CREATED);
//            final String articleTitle = StringEscapeUtils.escapeXml(article.getString(Article.ARTICLE_TITLE));
//
//            stringBuilder.append("<value><struct>");
//            stringBuilder.append("<member><name>dateCreated</name>").append("<value><dateTime.iso8601>").append(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(createDate)).append(
//                    "</dateTime.iso8601></value></member>");
//            stringBuilder.append("<member><name>description</name>").append("<value>").append(StringEscapeUtils.escapeXml(article.getString(Article.ARTICLE_CONTENT))).append(
//                    "</value></member>");
//            stringBuilder.append("<member><name>title</name>").append("<value>").append(articleTitle).append("</value></member>");
//            stringBuilder.append("<member><name>postid</name>").append("<value>").append(article.getString(Keys.OBJECT_ID)).append(
//                    "</value></member>");
//
//            stringBuilder.append("<member><name>categories</name>").append("<value><array><data>");
//            final String tagTitles = article.getString(Article.ARTICLE_TAGS_REF);
//            final String[] tagTitleArray = tagTitles.split(",");
//
//            for (int i = 0; i < tagTitleArray.length; i++) {
//                final String tagTitle = tagTitleArray[i];
//                stringBuilder.append("<value>").append(tagTitle).append("</value>");
//            }
//            stringBuilder.append("</data></array></value></member>");
//            stringBuilder.append("</struct></value>");
//        }

        return stringBuilder.toString();
    }

    /**
     * Builds categories (array of category info structs) with the specified preference.
     *
     * @return blog info XML
     * @throws Exception exception
     */
    private String buildCategories() throws Exception {
        final StringBuilder stringBuilder = new StringBuilder();
//        final List<JSONObject> tags = tagQueryService.getTags();
//        for (final JSONObject tag : tags) {
//            final String tagTitle = StringEscapeUtils.escapeXml(tag.getString(Tag.TAG_TITLE));
//            final String tagId = tag.getString(Keys.OBJECT_ID);
//
//            stringBuilder.append("<value><struct>");
//            stringBuilder.append("<member><name>description</name>").append("<value>").append(tagTitle).append("</value></member>");
//            stringBuilder.append("<member><name>title</name>").append("<value>").append(tagTitle).append("</value></member>");
//            stringBuilder.append("<member><name>categoryid</name>").append("<value>").append(tagId).append("</value></member>");
//            stringBuilder.append("<member><name>htmlUrl</name>").append("<value>").append(Latkes.getServePath()).append("/tags/").append(tagTitle).append("</value></member>");
//            stringBuilder.append("</struct></value>");
//        }

        return stringBuilder.toString();
    }

    /**
     * Builds blog info struct with the specified preference.
     *
     * @param preference the specified preference
     * @return blog info XML
     * @throws JSONException json exception
     */
    private String buildBlogInfo(final JSONObject preference) throws JSONException {
//        final String blogId = preference.getString(Option.ID_C_ADMIN_EMAIL);
//        final String blogTitle = StringEscapeUtils.escapeXml(preference.getString(Option.ID_C_BLOG_TITLE));
//        final StringBuilder stringBuilder = new StringBuilder("<member><name>blogid</name><value>").append(blogId).append(
//                "</value></member>");
//        stringBuilder.append("<member><name>url</name><value>").append(Latkes.getServePath()).append("</value></member>");
//        stringBuilder.append("<member><name>blogName</name><value>").append(blogTitle).append("</value></member>");

//        return stringBuilder.toString();
                return null;
    }

}
