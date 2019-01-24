package calcite.flink.v1_7.table.plan.cost;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.val;
import org.apache.calcite.rel.metadata.ChainedRelMetadataProvider;
import org.apache.calcite.rel.metadata.DefaultRelMetadataProvider;
import org.apache.calcite.rel.metadata.RelMetadataProvider;

/**
 * @author quxiucheng
 * @date 2019-01-22 17:01:00
 */
public class FlinkDefaultRelMetadataProvider {
    public static final RelMetadataProvider INSTANCE = ChainedRelMetadataProvider.of(Lists.newArrayList(
            FlinkRelMdRowCount.SOURCE,
            DefaultRelMetadataProvider.INSTANCE)
    );

}
