package org.eustrosoft.handlers.cms.dto;

import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.util.JsonUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.eustrosoft.core.json.Constants.JSON_DELIM;
import static org.eustrosoft.core.json.Constants.JSON_FORMAT_MASSIVE;

public class CMSData implements JsonConvertible {
    private List<CMSObject> content;

    public CMSData(List<CMSObject> content) {
        this.content = content;
    }

    public List<CMSObject> getContent() {
        return content;
    }

    public void setContent(List<CMSObject> content) {
        this.content = content;
    }

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJsonFormatted(
                JsonUtil.AsEntry.getRawParams(
                        "content", String.format(JSON_FORMAT_MASSIVE,
                                content.stream().map(d -> {
                                    try {
                                        return d.convertToString();
                                    } catch (JsonException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }).filter(Objects::nonNull).collect(Collectors.joining(JSON_DELIM))
                        )
                )
        );
    }
}
