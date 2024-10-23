package md.mirrerror.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "messages")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "message", columnDefinition = "LONGTEXT", nullable = false)
    private String message;

    @Column(name = "sender", nullable = false)
    private String sender;

    @Column(name = "timestamp", nullable = false)
    private String timestamp;

}