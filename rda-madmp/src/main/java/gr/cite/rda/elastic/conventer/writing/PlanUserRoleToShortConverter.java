package gr.cite.rda.elastic.conventer.writing;


import org.opencdmp.commonmodels.enums.PlanUserRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class PlanUserRoleToShortConverter implements Converter<PlanUserRole, Short> {
	@Override
	public Short convert(PlanUserRole source) {
		return source.getValue();
	}
}
