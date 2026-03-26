package kamkeel.npcdbc.data.form;

import java.util.Objects;

public class FormKey {
    public final String namespace;
    public final String name;

    public FormKey(String namespace, String name) {
        if (namespace == null || namespace.trim().isEmpty())
            throw new IllegalArgumentException("FormKey namespace must not be empty");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("FormKey name must not be empty");

        this.namespace = namespace.trim().toLowerCase();
        this.name = name.trim().toLowerCase();
    }

    public FormKey(String key) {
        if (key == null)
            throw new IllegalArgumentException("FormKey string must not be null");

        String normalized = key.trim().toLowerCase();
        int split = normalized.indexOf('/');
        if (split <= 0 || split == normalized.length() - 1)
            throw new IllegalArgumentException("FormKey must be in 'namespace/name' format: " + key);

        this.namespace = normalized.substring(0, split);
        this.name = normalized.substring(split + 1);
    }
    
    public static FormKey of(String namespace, String name){
        return new FormKey(namespace, name);
    }

    @Override
    public String toString() {
        return namespace + "/" + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof FormKey))
            return false;
        FormKey other = (FormKey) obj;
        return namespace.equals(other.namespace) && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, name);
    }
}
