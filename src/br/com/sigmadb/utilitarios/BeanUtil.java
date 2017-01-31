package br.com.sigmadb.utilitarios;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;


public class BeanUtil {
    static {
        
        ConvertUtils.register(new Converter() {
                public Object convert(Class type, Object value) {
                    SqlTimestampConverter sqlTimestampConverter = new SqlTimestampConverter();

                    if ((value == null) || (value.toString().length() < 1)) {
                        return null;
                    }

                    return sqlTimestampConverter.convert(type, value);
                }
            }, Timestamp.class);
    }

    /**
     * Realiza a cópia de valores do objeto de origem para o de destino, tomando como base para transferência as propriedades
     * que possuem nomes iguais em ambos os objetos.
     * @param origem Objeto contendo os valores de origem.
     * @param destino Objeto contendo os valores onde deverão ser salvo os valores da origem.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void copiaPropriedadesBean(Object origem, Object destino)
        throws IllegalAccessException, InvocationTargetException {
        BeanUtils.copyProperties(destino, origem);
    }
}
