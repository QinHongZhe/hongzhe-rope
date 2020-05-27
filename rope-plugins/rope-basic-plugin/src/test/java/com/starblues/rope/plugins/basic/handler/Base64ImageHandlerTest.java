package com.starblues.rope.plugins.basic.handler;

import com.google.common.collect.Maps;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import org.junit.Test;

import java.util.Map;

/**
 * 图片处理中测试
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-27
 */
public class Base64ImageHandlerTest {



    @Test
    public void handle() throws Exception {
        Base64ImageHandler base64ImageHandler = new Base64ImageHandler();

        Base64ImageHandler.Param param = new Base64ImageHandler.Param();
        param.setImageFieldKey("image");
        param.setImageNameRule("image-${name}-${id-card}.jpg");
        param.setImageSavePath("D://test/image/");
        param.setGenAccessPath(true);
        param.setAccessPathFiledKey("access");
        param.setAccessPathRule("/image/${imageName)");

        base64ImageHandler.setParam(param);

        Record record = new DefaultRecord();
        record.putColumn(Column.auto("name", "zhangzhuo"));
        record.putColumn(Column.auto("id-card", "6228261993"));

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("iVBORw0KGgoAAAANSUhEUgAAAOsAAABGCAYAAAAtiGPlAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAVBSURBVHhe7ZtNThtLFEa9mWyHgSXm2UN2kHgJWQIwzOTtIAOsZAYISyiSLRGQBRIT9CCE9x71/PWP+6/abttdTV9yjnQFdFf3vQNO36pye+AAwATICmAEZAUwArICGAFZAYyArABGQFYAIyArgBGQFcAIyApgBGQFMAKyAhgBWQGMgKwARkBWACMgK4ARkBXACMgKYARkBTACskJQnp6e3M3NjZtOp24ymbjT09PgoTzKp7zK/1ZAVgiCJLm8vHTn5+fu+vra3d/fu+fnZ/fy8pKMCIPurzzKp7zKrzregrTIGpiXf/9z/zz+cr/v/+5dqC7V1zZ3d3dRh7u9vQ0u5zqUX3WoHtVlGWQNiETwSdK3aFNYTT0vLi7c4+NjcqQfqB7VpfqsgqwB6WtHLYfqbAN1LgmhaaiPh4cHd3V1FY05OzuLQr/rmM6FRnUpn9UO2wNZ5+5wf+AGg9UxPJon4xeMR94x9TF0hz+T6z4ex/cocOxG6ZgVHH8cuNE4+aMBPjH6GruiNaGmmnUdVUKmG0B1oTGhUX3KZXEN2xtZV0kwPxoWZfWSSO+VMUPCLceslL4qL7LWo00crQ19zGaziph1obGhUZ2q1xo2O2uJSMDluOP4fvuHizv7UL5cp/WOy43J4ZX156Eb5uqMY7To1VvK+vWzG7/bcydfPecCxi6oS2nX1beZ1KSjliN0h1WdqtdadzXcWTPJ03OSaTluKdGK6W1DWfMPg3xEuZSncA9Nqf8sWbVpo49Jymgd6pOxSYRew6pea5tNBjtrOt7f+apSSx4dP8nypHJtNA0uPlSWuXaQdfppz30/mHnPtRPf3Mm7D27yw3euGLugFxD0uWaZbbpqGqG7q+pV3Zbogawdk++mtZ3VR1XW6PetZJ25yfs9N/70rXps0VXHJcEk9cnBF/dd5xbX6O9C9026cT6WD4Ef8XXrOvUu6I0h3w6wdl59IjYJXRsS1au6LfGqstZNL1dFdZq7JsoylmX1XZNEsUu3Jau63aqOWu2GsZyf3TQRT9cuu3J0LBs/P/hQeggo1uXcTVbJ5Vuv6qOZsoRNQ9eGRPUqjyV62Vm1Rl23qxvLGktRi69zNuys1Sm1JCyuYzeWtdF61C9rJuZC2sqxkqzvv7h5cm0WSef2nkNWC/RD1pI0kazrOl3XspbGdt1Za2UtTJ+TDry8VxphOyvT4G7ozwZTrpN22lk9D4U08rKW5d1eVoVvzZrGhrKqW3vvk0R0Tdg1KxtM3fDKsiai7o/cSD+Tf/Lmshbl8kZOpLhjL3ItRIuON+2skdT5B4PqTqbE3jrWyRpH1h31d9z9sg6piKXdrLMq0u5aFb8udoGPbrrhFWWNRa1MNSv/+MVYjt+4sypfbnyDXIrh0V/ZWjUvZnrfrTpri6HOWliHxvJmD4FmsQu8FNENvdxgeiv4pGg7qhtKcYfe9KWKXeF1w/Aga0C6+dZNdRq8aVdt41s36lKSjRf5w4GsAfnTvs/KV+TCgqyBkQh9/V6r6mrzi+dCmzYSoq7DvhaqR3VZ21TKg6zQOupcmmpqbejbdOoS5VcdqsdqR01BVgiC1oTaxNGuqz4m0eeamoaGllf3Vx7lU17lVx0W16hlkBWCIkk09dQLCHpjKN1MChnKo3zK+xYkTUFWACMgK4ARkBXACMgKYARkBTACsgIYAVkBjICsAEZAVgAjICuAEZAVwAjICmAEZAUwArICGAFZAYyArABGQFYAIyArgBGQFcAIyApgBGQFMAKyAhgBWQGMgKwARkBWACMgK4ARkBXABM79D6SX0NJ91sj1AAAAAElFTkSuQmCC");
        record.putColumn(Column.auto("image", stringBuilder.toString()));

        Record handle = base64ImageHandler.handle(record);
        System.out.println(handle);
    }

}
