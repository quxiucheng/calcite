package calcite.flink.v1_1.table;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.TimeZone;

/**
 * @author quxiucheng
 * @date 2019-01-21 16:13:00
 */
public class TableConfig implements Serializable {


    private static final long serialVersionUID = 7906856450476850886L;

    TableConfig DEFAULT = new TableConfig();


    /**
     * Defines the timezone for date/time/timestamp conversions.
     */
    @Getter
    @Setter
    private TimeZone timeZone = TimeZone.getTimeZone("UTC");

    /**
     * Defines if all fields need to be checked for NULL first.
     */
    @Getter
    @Setter
    private Boolean nullCheck = true;


    /**
     * Defines if efficient types (such as Tuple types or Atomic types)
     * should be used within operators where possible.
     */
    @Getter
    @Setter
    private Boolean efficientTypeUsage = false;


}
