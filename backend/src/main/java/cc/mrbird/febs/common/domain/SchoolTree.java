package cc.mrbird.febs.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolTree implements Serializable {
    private String title;
    private String value;
    private String key;
    private List<SchoolTree> children;
    private Boolean selectable;
}
