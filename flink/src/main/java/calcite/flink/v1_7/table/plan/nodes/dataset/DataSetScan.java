package calcite.flink.v1_7.table.plan.nodes.dataset;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.core.TableScan;

/**
 * @author quxiucheng
 * @date 2019-02-21 16:52:00
 */
public class DataSetScan extends TableScan {
    protected DataSetScan(RelOptCluster cluster, RelTraitSet traitSet, RelOptTable table) {
        super(cluster, traitSet, table);
    }
}
