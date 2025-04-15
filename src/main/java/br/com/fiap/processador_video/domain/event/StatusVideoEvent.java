package br.com.fiap.processador_video.domain.event;

import java.util.UUID;

import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class StatusVideoEvent {

    private UUID videoId;
    private VideoStatus status;
}
