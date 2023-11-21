package cc.mrbird.febs.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
@AllArgsConstructor
public class Column {
    private String title;
    private String dataIndex;
    private Map<String, String> scopedSlots;
    private List<Column> children;
}
