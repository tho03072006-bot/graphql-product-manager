package vn.iotstar.catalog.controller;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
@Component
public class GraphQlExceptionResolver extends DataFetcherExceptionResolverAdapter {
    @Override protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        ErrorType type; String message;
        if (ex instanceof NoSuchElementException) { type = ErrorType.NOT_FOUND; message = ex.getMessage(); }
        else if (ex instanceof ConstraintViolationException validation) {
            type = ErrorType.BAD_REQUEST;
            message = validation.getConstraintViolations().stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).sorted().collect(Collectors.joining("; "));
        } else if (ex instanceof IllegalArgumentException) { type = ErrorType.BAD_REQUEST; message = ex.getMessage(); }
        else if (ex instanceof DataIntegrityViolationException) { type = ErrorType.BAD_REQUEST; message = "Dữ liệu đang được tham chiếu hoặc vi phạm ràng buộc database."; }
        else return null;
        return GraphqlErrorBuilder.newError(env).errorType(type).message(message).build();
    }
}
