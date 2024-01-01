package site.goldenticket.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.restdocs.snippet.Attributes.Attribute;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.snippet.Attributes.key;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
public abstract class ApiDocumentation {

    private static final String BASE_URL = "localhost";

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    protected OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(modifyUris().host(BASE_URL).removePort(), prettyPrint());
    }

    protected OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint());
    }

    protected Attribute getTimeFormat() {
        return key("format").value("hh:mm");
    }

    protected Attribute getDateFormat() {
        return key("format").value("yyyy-MM-dd");
    }

    protected Attribute getPhoneFormat() {
        return key("format").value("000-0000-0000");
    }

    protected Attribute getEmailFormat() {
        return key("format").value("email@gmail.com");
    }
}
