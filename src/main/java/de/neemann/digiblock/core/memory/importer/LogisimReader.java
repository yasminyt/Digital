/*
 * Copyright (c) 2019 Helmut Neemann.
 * Use of this source code is governed by the GPL v3 license
 * that can be found in the LICENSE file.
 */
package de.neemann.digiblock.core.memory.importer;

import de.neemann.digiblock.core.Bits;
import de.neemann.digiblock.lang.Lang;

import java.io.*;

/**
 * Reader to read the original Logisim hex file format
 */
public class LogisimReader implements ValueArrayReader {
    private Reader reader;

    /**
     * Creates a new instance
     *
     * @param file the file to read
     * @throws FileNotFoundException FileNotFoundException
     */
    public LogisimReader(File file) throws FileNotFoundException {
        this(new FileReader(file));
    }

    /**
     * Creates a new instance
     *
     * @param reader the reader to used
     */
    public LogisimReader(Reader reader) {
        this.reader = reader;
    }

    @Override
    public void read(ValueArray valueArray) throws IOException {
        try (BufferedReader br = new BufferedReader(reader)) {
            /*
             * 去掉 v2.0 raw 这样的头部信息
             */
//            String header = br.readLine();
//            if (header == null || !header.equals("v2.0 raw"))
//                throw new IOException(Lang.get("err_invalidFileFormat"));
            String line;
            int pos = 0;
            while ((line = br.readLine()) != null) {
                try {
                    String[] datas = line.split("\\s+");
                    for (String s : datas) {
                        String data = s;
                        int p = data.indexOf('#');
                        if (p >= 0)
                            data = data.substring(0, p).trim();
                        else
                            data = data.trim();

                        int rle = 1;
                        p = data.indexOf('*');
                        if (p > 0) {
                            rle = Integer.parseInt(line.substring(0, p));
                            data = data.substring(p + 1).trim();
                        }

                        if (data.length() > 2 && data.charAt(0) == '0' && (data.charAt(1) == 'x' || data.charAt(1) == 'X'))
                            data = data.substring(2);

                        if (data.length() > 0) {
                            long v = Bits.decode(data, 0, 16);
                            for (int j = 0; j < rle; j++) {
                                valueArray.set(pos, v);
                                pos++;
                            }
                        }
                    }
                    /*
                     *  每一行的数据通过空格分隔，所以需要定义新的读取方式
                     */
//                    int p = line.indexOf('#');
//                    if (p >= 0)
//                        line = line.substring(0, p).trim();
//                    else
//                        line = line.trim();
//
//                    int rle = 1;
//                    p = line.indexOf('*');      // 3*ff 表示有3个连续的 ff
//                    if (p > 0) {
//                        rle = Integer.parseInt(line.substring(0, p));
//                        line = line.substring(p + 1).trim();
//                    }
//
//                    if (line.length() > 2 && line.charAt(0) == '0' && (line.charAt(1) == 'x' || line.charAt(1) == 'X'))
//                        line = line.substring(2);
//
//                    if (line.length() > 0) {
//                        long v = Bits.decode(line, 0, 16);
//                        for (int i = 0; i < rle; i++) {
//                            valueArray.set(pos, v);
//                            pos++;
//                        }
//                    }
                } catch (Bits.NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }
    }


}
