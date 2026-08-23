package org.unitedlands.economy.classes;

public class Currency {
    private String key;
    private String symbol;
    private String format;
    private String nameSingular;
    private String namePlural;

    public Currency(String key, String symbol, String format, String nameSingular, String namePlural) {
        this.key = key;
        this.symbol = symbol;
        this.format = format;
        this.nameSingular = nameSingular;
        this.namePlural = namePlural;
    }

    public String getKey() {
        return key;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getFormat() {
        return format;
    }

    public String getNameSingular() {
        return nameSingular;
    }

    public String getNamePlural() {
        return namePlural;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Currency other = (Currency) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

    

}
