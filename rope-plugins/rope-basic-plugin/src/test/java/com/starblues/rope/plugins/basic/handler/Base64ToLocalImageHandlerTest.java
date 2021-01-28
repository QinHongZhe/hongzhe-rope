package com.starblues.rope.plugins.basic.handler;

import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import org.junit.Test;

/**
 * 图片处理中测试
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-27
 */
public class Base64ToLocalImageHandlerTest {



    @Test
    public void handle() throws Exception {
        Base64ToLocalImageHandler base64ToLocalImageHandler = new Base64ToLocalImageHandler();

        Base64ToLocalImageHandler.Param param = new Base64ToLocalImageHandler.Param();
        param.setImageFieldKey("image");
        param.setImageNameRule("image-${name}-${id-card}.jpg");
        param.setImageSavePath("D://test/image/");
        param.setGenAccessPath(true);
        param.setAccessPathFiledKey("access");
        param.setAccessPathRule("/image/${imageName}");

        base64ToLocalImageHandler.setParam(param);

        Record record = new DefaultRecord();
        record.putColumn(Column.auto("name", "zhangzhuo"));
        record.putColumn(Column.auto("id-card", "6228261993"));

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("/9j/4AAQSkZJRgABAQEAYABgAAD/4SGERXhpZgAATU0AKgAAAAgABgALAAIAAAAmAAAAVgExAAIA\n" +
                "AAAOAAAAfAEyAAIAAAAUAAAAiodpAAQAAAABAAAIqoglAAQAAAABAAARAOocAAcAAAgMAAAAngAA\n" +
                "IR5XaW5kb3dzIFBob3RvIEVkaXRvciAxMC4wLjEwMDExLjE2Mzg0AHd3dy5tZWl0dS5jb20AMjAx\n" +
                "ODoxMDoyMiAxMDowNjo1MQAc6gAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEkAMAAgAAABQAAAjgkpEAAgAAAAQ4MTMAoAEAAwAA\n" +
                "AAEAAQAA6hwABwAACAwAAAj0AAAAADIwMTg6MTA6MjIgMTA6MDU6NDMAHOoAAAAIAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAeocAAcA\n" +
                "ABAMAAAREgAAAAAc6gAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAYBAwADAAAAAQAGAAABGgAFAAAAAQAAIWwBGwAFAAAAAQAAIXQBKAAD\n" +
                "AAAAAQACAAACAQAEAAAAAQAAIaQCAgAEAAAAAQAADBsAAAAAAAAAYAAAAAEAAABgAAAAAf/bAEMA\n" +
                "BwUGBgYFBwYGBggIBwkLEgwLCgoLFxARDRIbFxwcGhcaGR0hKiQdHyggGRolMiUoLC0vMC8dIzQ4\n" +
                "NC43Ki4vLv/bAEMBCAgICwoLFgwMFi4eGh4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4u\n" +
                "Li4uLi4uLi4uLi4uLi4uLi4uLv/AABEIAKIAdAMBEQACEQEDEQH/xAAfAAABBQEBAQEBAQAAAAAA\n" +
                "AAAAAQIDBAUGBwgJCgv/xAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEFEiExQQYTUWEHInEUMoGR\n" +
                "oQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdo\n" +
                "aWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU\n" +
                "1dbX2Nna4eLj5OXm5+jp6vHy8/T19vf4+fr/xAAfAQADAQEBAQEBAQEBAAAAAAAAAQIDBAUGBwgJ\n" +
                "Cgv/xAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIygQgUQpGhscEJIzNS8BVi\n" +
                "ctEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqC\n" +
                "g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2dri4+Tl\n" +
                "5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/AM/bgqvYYFYM6ytdDfLbxj+Jy1VcLHuXgOzCaDEc\n" +
                "cuSa55bkydjoxCyPgDin1J5rlqSJJ4vLkGa1TMmcZ4iNjYy+TcTKpYZGetaRixORyNz4zksbdorF\n" +
                "8tnG4jkCt1EzbOauvGWp3MjN9pkB9nIqiLFjTPG2s2jlkvWZe6yfNSdhpHaaL8S0kdI9ShVVJwZU\n" +
                "PT6iocUVc9Hsby2voFntplkjYZBU5qXEEy0KkYtACUmMM0AfMh5c+gBqGdhFCvm6ki9kUCk2M+jP\n" +
                "D1v9m0m1ixgiNc/XFZJXZzzZottUFmIAHc1tyEXOS8R+LLCwhlS1u42ux0XqKuMbMTZ4vr+vXGo3\n" +
                "T3Uszu2cEbuB6Y9q6FZCSucy+oP553nINA+UqifDtg8ZzUtisSi4OwY6nrTux2HLdbeAaLsTidJ4\n" +
                "d8ValpL/AOi3LKmQSh5U/hS3JseyeFPHljrGy3u9ttdnjBPyv9D/AEqXEEzuAwIyDxUMYvakMbsU\n" +
                "9QD9aLDPmbHX3wKg6i14Ztzd60FAyHlC/hnFTJj2R9GwKFiVfQUqZzSOE+JXiSXS7dbC0IWaVcs3\n" +
                "oPaumKJR4VPPcz3DElmJOST3ptlKI46fMAGkyN3NJTL5bGTcWbCN5emDRzj5SCNWX7w7U+YlxGOW\n" +
                "3HFO4lEZuYZNFw5SWKZg3WnclovQXskLKysQR3HahMTR7H8OPHbzSx6Zqk24HCxSseQfQ0OJGx68\n" +
                "rBgCDkGs2rD3HUgPmNm2gse2W/KsztsdN8MbXzdXtyR0y5NTJBU0R7bdXEVpbvPMwVEGSTVwicjP\n" +
                "BvFt7Jr2vyyQglOi57KK0k7I1hC42HQfIijLr8zEE5rCUzqjTINWhMYwVwMYpRkKcTnHjVlaM/Wm\n" +
                "2SolCeEKpyMFelOMgcCusG4EYyatyEqZHNb7VAx1OKFIbgM+z4ljwOpxVcxm4D7iAqcgU1ITgJZX\n" +
                "LQSgjIINWncxlE+kPhx4og1jS47WWUC8hUBlJ5YeopSRC0O4qAPmC5P7l/cBR+dZo9BbHpnwptP3\n" +
                "00+OEjC/if8A9VIyrbGh8RdYKSx6arYQL5kmD19B+lbRRhFXMXwFoXnQS6ncJuWRv3YPcCs5O51Q\n" +
                "0Opu9LGDIygvjgdlFYSibwlc43WIEkV42UVknY0cThbmxlimPcZ6ir5iOUY9n5ikOOD+lNMfKQpY\n" +
                "GJ8hyR6EU3IXKQz2xaQfL0qlITiMmtjgEDlSGFNSJ5SSSJZE3D0oUiXEwLlSjtjsa2iznlE2/Cl7\n" +
                "dQalbvaylJg3y+/tW5lKJ9K6Vqzz2EUlxEySEc9OfeosiLHz3PlmhT+85P4D/wDXWK2O+J7X8OIB\n" +
                "baHJcMMF2Jz7ClHcwrPU8/166bW9bnePO2aQIn+7nFat2QoRPUtLjFlp0NuF2qigAVg3Y6FG5JNO\n" +
                "pUk+lQ5XNYwscHrAxKxHU+lYPc2Ry9yBk5FAFJiAadwIj9KLhYjYc1SYmiJgKLk2GFFwcU0xNGNd\n" +
                "23zSYHXmtoSOecSjpZeK9XBIKsCCO1dUWcsz6606CM2UBO1iUBz68VNjM+cIV33yL2jUD8TWVzv6\n" +
                "nsd7L/ZPgNFztkkjCjHq1EDllrI5HwZYG81WKRh8iHcaJM2itLnqd/F8i7etZyLpS1MLUH8sY3dq\n" +
                "xaOtM5TUnU5JOTUMZzV2QSSKQWM9sbqYDxHkZoAikSgCuV9KaAbg9DTuIhnQYOBVRZlKJz8I8u9Z\n" +
                "veuynI5KiPqrwneC78O6fOcZaFc/XFWc9jwjw7bm71MIOTJKFH8qwaO1vQ9f8fW3/FOxBRxGy4H6\n" +
                "VUTlWrKHw6tgI5JSOnArOW5tJ2R1eq3McEJdzjFA4KzPLta8Sqblxn5R0xUNG6mYEuuwTgjdg1HK\n" +
                "WpFKW7V+hzUuJomRiQE9agosCUBetMCCSUUCIxIuaYAcGiwhrDKGmiWcvOpW7OBzniuumcdQ928J\n" +
                "HUIfD9mis6/JnGM1rc57HO/DGx87VrYkZ8seYaxe9jpqOyseteKLcXGh3UeMsEJA+nNWlY5o2uY/\n" +
                "gqMQ6KZCMEuaxnubbuxT8RtPqB8i3zgdT6UcxuoaHlWvaa0cjKznINK5XKcncWzxvwx600Mmt3lV\n" +
                "gN2RUSRcTRDMBk8VFi7kU13sUnNTYdzIudTmBO0mtFEVyvHqk5bkHFVyE3NW0vWcrk0uUVzYjPmC\n" +
                "kkTsY727yatHDEgZy3A9a6IOxjJXOzl8Z61p0r2aeTIkZwpdMkDHSruZ+zZ3XwqstsNzeFepCKf1\n" +
                "P9KzWrJqs0df1G6fU3sYyyMoyjIfbPNQ5+9Y3p0ouHMcnbeIdRe4uNNhkaGG1dhLIkYJZtx9eg4q\n" +
                "5omMbu5VvtbureN0tbi8lnb+6i4x78VmkaM4bVtZvkcG72OW5wRz+lXyXFzGa12sqB2jKg9waXLY\n" +
                "aYtuP3oYH5O5PapNEzbmiQ24ZWVuOxzUMZg3aOc4BNCQ7lNIF3ZlIGfU1SAtpFacDKnNVcVyZYYx\n" +
                "yhH50hGxpx/hYUJEs0PCmnm98XZI+WFS/wCn/wBeqvYlFDWI/wDiZ3PH8dK5rZHvngqy+xaDbqRh\n" +
                "nG8/jWqPNm7sx/E6CDU5bnHWHaPqTj+hrGa1uddGV42EXSUi1mSUQAQ3FqqswHBYH+eDTcrkRfvH\n" +
                "O6tCtlM7RAY5GPSoTsdCVzz3WbKO5kLFOa0UiHEyRZscRBc+gpuQ1E37DSjHEVZAWdSoDep+UfqQ\n" +
                "fwrO5a0H3tlYW8RhihTK/KWA5P41DkWlc5y6t1gdUUHa43KM5xVozM27ickhTjjiqQFG3S5WX5iw\n" +
                "HsaqwbGhE86SfMpZPeiwtzTRpEheWJmVlGRg8flStcnY63QPEJ0q2EkdtF9onyZHYHIGeBTlsEIN\n" +
                "u5laofOvpZgcByG/MVnZmp9LQosUSxoMKowBXQjyutjk/GlvIwhlUZG9c/r/AI1nUWh2YeSV0a1y\n" +
                "yfYDu67ePWs0xRjeR5fr85WVvnJGe9I6UrHLSkytxQNl2wtow44yfWkwR2NvbRmzKLAjyHkMf4fp\n" +
                "VCa1ON1e0MLsCMc1mzVKyOcu1VzGW+8mR+FWmZtFWWEPzincpIaIRjgYp3E0WYrZT1ouTaxdSEYV\n" +
                "ABgkZpp6mbV2bmi6MdV1dbfH7sHc5/2QBQ02zWMlCJR12HytWuo0GFV8AU9R3PpICtkeSZuuQLNp\n" +
                "8obqo3A+9TJGlN2kZeoE/ZgAeornkd1NHnOuWxDMWNI1OfCBCaY7E1vcrHIKBWO90LevMy9V4pNj\n" +
                "SOV8VshuH21DZojiroYycU0SyCNtw4phYsIoOOKYrEygDtTuJl+yQMRxTMXoemfDjTttnf37LzI+\n" +
                "1CfQD/H+VbJaHPOd5WPPPEy41y8/3zUHQmfRVbHmkF3CJ4JIScBhjNA07Mxb+LYAh5xXNNHoUZXO\n" +
                "G8QquOnNSjc4u5YgkUCIrBl/tC3eUZhWQFvpmi5SNp/GjS6rJZMnlL91HHSpuO1jntZ1F2uX3Nnm\n" +
                "paLTMj+0IXlEDH5jVJEsSDiRlB4Bq2hovJSExx5p2M2aWmKd2fStIoxmz3TQreO20K0iiXAMYJ9y\n" +
                "RmtnscSd5HjnilANcuuP4qxO1bH0BWx5whFAGRqoyawmdlB6HB+IYsg8d+tZ3OpnGXMBaTaBnNAX\n" +
                "LUVgqxYYc4pFoz7rT7Xa7Mo3n+KmkTKTOb1GAs5AY5HenYXMzPisSJfMB5plKRpxx7RmkUSqSOKQ\n" +
                "rliNdxqkQ2d54F0NNSuWM6nyYwC2O/oP51vBHHWnY9eEapEEUcAYAq2cyPEPFsYGvXXH8VYnoQ+E\n" +
                "92rU84SgDM1UYQNWVRHTh3qcTralkOBWJ3PY5NmSKRmftQSVX1SOUssbjA60WGmZMl9bnd5k4B7D\n" +
                "rRcbi2Yskil3cuMHpzRcTi0RCQA8HIpgkWEmVsUiiQHNMRes13MAO9aRRnNnufgbTvsOixuww83z\n" +
                "nj8v0rdKx59R3Z0rdDQyUeO+LI8a5ccdTmsjvh8J7HmtDzwJoA5XxvqS6XbWlw5/dmYI/wBCD/hU\n" +
                "yV0bUpWkY91JHPb70YMpGQR3rkueiefa+WMronemiTn7bTg8hMrNtPXDEVQ7lbU7HT4lJQuD67s0\n" +
                "aFxkYLxo5wJjRZGnMCQzr92Y4p2M2y3aiVW+c596RLZqRnJFCE2b+gtZpqFsb59luZBvPtW0DnrS\n" +
                "0Poa2MZgjMRBjwNuOmK2ZwkrdKAR5r4lsXl1WR1Xtz+ZrOx1xloYGq/FO8kk22EUcKf7Q3E1vyHM\n" +
                "lco2vjTXbotNLfuEHZQAP5VSpky0MzxD4jvdYRYbm5LqqkquAOfXjvV+zshRlqP8F+JgG/sW/kww\n" +
                "4hc9CP7v+FebWhys9SlLmibmrWQcl0GQe9ZItmBJbyBGVRTbEYV9YXEucjAoTRaZjf2dIjEmrugJ\n" +
                "o4SvUUriJQuDRcViwjbF3scAdaaE3oZ82pGW5HlnEanj3966qUTkqM9y+FXiT7Zaf2XcyZljGYye\n" +
                "6+n4Vc1Y5mrHo8rbUJqEJHPXdust3Mxzw2P0FKxsmfLSy7nyDXVYzTN+3m8qyPuK2hEzk7mTHdlb\n" +
                "iMtzyVPsDTlsC0RX1VJIpPPjJDpyMdxXNXpc0TejUszr/C3jOO4iSy1SQBgMLKe/1/xryZR5T0ou\n" +
                "6OwzbgB/lKHkEd6LhYw9WuIcsEwBRcdjn5JUJPPWlcrlIGePoKpMTiQtIi5JNNE2Od1bVnaRoUBC\n" +
                "Dg+9bwjcylKxUtpyWBrsirHJN3Ow8K6zNp2oQXMT7XjYH6+1VLUzZ9O2d7Hf6TFeRHKSIGrBqzFF\n" +
                "ajo4N+5/7xz+gpls+PNPDSTquD1rqRl0O+TQJl083N9J5C4+WPGWP+FXzWMb6nMajYeQqSxbyGfG\n" +
                "DzTUrlE93GJosY+YUPUFKzOPvY5LS43qpCk/lXnV6R6FGqbGmeJb61jERZpIv7rdvpXF7M6udMtX\n" +
                "WvmfnawPelyBzFIam/PWlyFKY5LyRux5q1AHMsB3Zec1SiTzGVqcIVDJt59cV0UznqGbBKwBGDXS\n" +
                "mcrZpWNw6ODznNMSZ9BfDbxdp66B/ZuoXSxSoSEL8Ag+9ZziC3PRNKu0nsYpFYEHjrUFs4qx0rS0\n" +
                "u0ZNNtFIYYIgUY/StzBnQX1naPFh7WFh6NGDQZoyrjTdOKIDYWuM5x5S+/tQjToQ/wBmab83/Evt\n" +
                "f+/K/wCFWZ9Sje6TpTA7tMszz3gX/Cspm9Mrro2j4/5BVj/4Dp/hXIzpQv8AY+kf9Aqy/wC/C/4U\n" +
                "DF/sfScf8guy/wC/C/4Uholi0nSv+gZZ/wDfhf8ACqAlXStL/wCgbaf9+V/wpAyO40nSmjIbTLM/\n" +
                "WBf8KuBnIqpo2j5P/Epsf/AdP8K3MWTR6PpA6aXZf9+F/wAKaEi9b6Zpqq23T7UdOkK/4UMaOr0e\n" +
                "KKKyCRRIiBjhVUAVA2f/2Q==\n");
        record.putColumn(Column.auto("image", stringBuilder.toString()));

        Record handle = base64ToLocalImageHandler.handle(record);
        System.out.println(handle);
    }

}