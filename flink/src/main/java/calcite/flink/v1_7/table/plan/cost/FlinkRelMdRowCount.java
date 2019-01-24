package calcite.flink.v1_7.table.plan.cost;

import lombok.val;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Calc;
import org.apache.calcite.rel.metadata.ReflectiveRelMetadataProvider;
import org.apache.calcite.rel.metadata.RelMdRowCount;
import org.apache.calcite.rel.metadata.RelMetadataProvider;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.util.BuiltInMethod;

/**
 * @author quxiucheng
 * @date 2019-01-22 16:34:00
 */
public class FlinkRelMdRowCount extends RelMdRowCount {

    public static RelMetadataProvider SOURCE = ReflectiveRelMetadataProvider.reflectiveSource(
            BuiltInMethod.ROW_COUNT.method,
            this);

    @Override
    public Double getRowCount(Calc rel, RelMetadataQuery mq) {
        return super.getRowCount(rel, mq);
    }

}
