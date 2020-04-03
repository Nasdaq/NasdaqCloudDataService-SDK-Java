package com.nasdaq.ncdsclient.news;

import org.apache.avro.generic.GenericRecord;

public class News {

    public final static String CONSTANT_RELEASE_TIME = "ReleaseTime";
    public final static String CONSTANT_TRANSMISSION_ID = "TransmissionID";
    public final static String CONSTANT_REVISION_ID = "RevisionID";
    public final static String CONSTANT_RETRACT = "Retract";
    public final static String CONSTANT_STORY_TYPE = "StoryType";
    public final static String CONSTANT_TECHNICAL_STORY = "TechnicalStory";
    public final static String CONSTANT_METADATA = "Metadata";
    public final static String CONSTANT_HEADLINE = "Headline";
    public final static String CONSTANT_BODY = "Body";
    public final static String CONSTANT_TICKERS = "Tickers";
    public final static String CONSTANT_COPYRIGHT = "Copyright";
    public final static String CONSTANT_ISIN = "ISIN";
    public final static String CONSTANT_ARTICLE_IMAGE = "ArticleImage";


    private String ReleaseTime;
    private String TransmissionID;
    private String RevisionID;
    private String Retract;
    private String StoryType;
    private String TechnicalStory;
    private String Metadata;
    private String Headline;
    private String Body;
    private String Tickers;
    private String Copyright;
    private String ISIN;
    private String ArticleImage;

    public String getReleaseTime() {
        return ReleaseTime;
    }

    public News setReleaseTime(String releaseTime) {
        ReleaseTime = releaseTime;
        return this;
    }

    public String getTransmissionID() {
        return TransmissionID;
    }

    public News setTransmissionID(String transmissionID) {
        TransmissionID = transmissionID;
        return this;
    }

    public String getRevisionID() {
        return RevisionID;
    }

    public News setRevisionID(String revisionID) {
        RevisionID = revisionID;
        return this;
    }

    public String getRetract() {
        return Retract;
    }

    public News setRetract(String retract) {
        Retract = retract;
        return this;
    }

    public String getStoryType() {
        return StoryType;
    }

    public News setStoryType(String storyType) {
        StoryType = storyType;
        return this;
    }

    public String getTechnicalStory() {
        return TechnicalStory;
    }

    public News setTechnicalStory(String technicalStory) {
        TechnicalStory = technicalStory;
        return this;
    }

    public String getMetaData() {
        return Metadata;
    }

    public News setMetaData(String metadata) {
        Metadata = metadata;
        return this;
    }

    public String getHeadline() {
        return Headline;
    }

    public News setHeadline(String headline) {
        Headline = headline;
        return this;
    }

    public String getBody() {
        return Body;
    }

    public News setBody(String body) {
        Body = body;
        return this;
    }

    public String getTickers() {
        return Tickers;
    }

    public News setTickers(String tickers) {
        Tickers = tickers;
        return this;
    }

    public String getCopyright() {
        return Copyright;
    }

    public News setCopyright(String copyright) {
        Copyright = copyright;
        return this;
    }

    public String getISIN() {
        return ISIN;
    }

    public News setISIN(String ISIN) {
        this.ISIN = ISIN;
        return this;
    }

    public String getArticleImage() {
        return ArticleImage;
    }

    public News setArticleImage(String ArticleImage) {
        this.ArticleImage = ArticleImage;
        return this;
    }


    @Override
    public String toString(){
        String newsString = "";
        newsString+="ReleaseTime: " + this.ReleaseTime + " \n"+
                "TransmissionID: " + this.TransmissionID + " \n"+
                "RevisionID: " + this.RevisionID + " \n"+
                "Retract: " + this.Retract + " \n"+
                "StoryType: " + this.StoryType + " \n"+
                "TechnicalStory: " + this.TechnicalStory + " \n"+
                "metaDataNode: " + this.Metadata + " \n"+
                "HeadLine: " + this.Headline + " \n"+
                "Body: " + this.Body + " \n"+
                "Tickers: " + this.Tickers + " \n"+
                "CopyRight: " + this.Copyright + " \n"+
                "ISIN: " + this.ISIN + " \n"+
                "ArticleImage: " + this.ArticleImage;
        return newsString;
    }

    public static News newsBuilder(GenericRecord record){
        News news = new News();
        news.setReleaseTime(record.get(CONSTANT_RELEASE_TIME).toString());
        news.setTransmissionID(record.get(CONSTANT_TRANSMISSION_ID).toString());
        news.setRevisionID(record.get(CONSTANT_REVISION_ID).toString());
        news.setRetract(record.get(CONSTANT_RETRACT).toString());
        news.setStoryType(record.get(CONSTANT_STORY_TYPE).toString());
        news.setTechnicalStory(record.get(CONSTANT_TECHNICAL_STORY).toString());
        news.setMetaData(record.get(CONSTANT_METADATA).toString());
        news.setHeadline(record.get(CONSTANT_HEADLINE).toString());
        news.setBody(record.get(CONSTANT_BODY).toString());
        news.setTickers(record.get(CONSTANT_TICKERS).toString());
        news.setCopyright(record.get(CONSTANT_COPYRIGHT).toString());
        news.setISIN(record.get(CONSTANT_ISIN).toString());
        news.setArticleImage(record.get(CONSTANT_ARTICLE_IMAGE).toString());
        return news;
    }

}