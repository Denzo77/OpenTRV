/*
The OpenTRV project licenses this file to you
under the Apache Licence, Version 2.0 (the "Licence");
you may not use this file except in compliance
with the Licence. You may obtain a copy of the Licence at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the Licence is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the Licence for the
specific language governing permissions and limitations
under the Licence.

Author(s) / Copyright (s): Damon Hart-Davis 2014



Note from pycrc 0.8.1 README:

    Copyright of the generated source code
    ======================================

    Prior to v0.6, pycrc was released under the GPL and an additional addition to
    the licence was required to permit to use the generated source code in products
    with a OSI unapproved licence. As of version 0.6, pycrc is released under the
    terms of the MIT licence and such an additional clause to the licence is no
    more required.
    The code generated by pycrc is not considered a substantial portion of the
    software, therefore the licence does not cover the generated code, and the
    author of pycrc will not claim any copyright on the generated code.
*/

package uk.org.opentrv.comms.util.crc;

/**Implementation of 7-bit CRC of polynominal 0x5B (Koopman).
 * Mirrors C/C++/etc implementations used elsewhere.
 * <p>
 * Polynomial 0x5B (1011011, Koopman) = (x+1)(x^6 + x^5 + x^3 + x^2 + 1) = 0x37 (0110111, Normal)
 * <p>
 * See http://users.ece.cmu.edu/~koopman/roses/dsn04/koopman04_crc_poly_embedded.pdf
 * Also: http://users.ece.cmu.edu/~koopman/crc/0x5b.txt
 * Also: http://www.ross.net/crc/crcpaper.html
 * Also: http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * <p>
 * Also see PYCRC http://www.tty1.net/pycrc/
 * from which checksums and code fragments have been generated.
 * <p>
 * For PYCRC using following parameters:
<pre>
./pycrc.py -v --width=7 --poly=0x37 --reflect-in=false --reflect-out=false --xor-in=0 --xor-out=0 --algo=bbb
Width        = 4
Poly         = 0x37
ReflectIn    = False
XorIn        = 0x00
ReflectOut   = False
XorOut       = 0x00
Algorithm    = bit-by-bit

0x4
</pre>
 */
public final class CRC7_5B
    {
    private CRC7_5B() { /* prevent instance creation */ }

    /**Update 7-bit CRC with next byte; result always has top bit zero.
     * Polynomial 0x5B (1011011, Koopman) = (x+1)(x^6 + x^5 + x^3 + x^2 + 1) = 0x37 (0110111, Normal)
     * <p>
     * Should maybe initialise with 0xff.
     * <p>
     * See: http://users.ece.cmu.edu/~koopman/roses/dsn04/koopman04_crc_poly_embedded.pdf
     * <p>
     * Should detect all 3-bit errors in up to 7 bytes of payload,
     * see: http://users.ece.cmu.edu/~koopman/crc/0x5b.txt
     * <p>
     * For 2 or 3 byte payloads this should have a Hamming distance of 4 and be within a factor of 2 of optimal error detection.
     * <p>
     * TODO: provide table-driven optimised alternative,
     *     eg see http://www.tty1.net/pycrc/index_en.html
     *     or see https://leventozturk.com/engineering/crc/
     */
    public static byte crc7_5B_update(byte crc, final byte datum)
        {
        for(int i = 0x80; i != 0; i >>>= 1)
            {
            boolean bit = (0 != (crc & 0x40));
            if(0 != (datum & i)) { bit = !bit; }
            crc <<= 1;
            if(bit) { crc ^= 0x37; }
            }
        return((byte) (crc & 0x7f));
        }

    /**Overloading to make use with int-typed arguments (eg literal constants) easier. */
    public static byte crc7_5B_update(final int crc, final int datum)
        { return(crc7_5B_update((byte)crc, (byte)datum)); }



    // Based on PYCRC-generated bit-by-bit code.
    // ./pycrc.py -v --width=7 --poly=0x37 --reflect-in=false --reflect-out=false --xor-in=0 --xor-out=0 --algo=bbb --generate=X
    /**Initialisation. */
    public static byte bbb_init() { return(0); }

    /**Update
<pre>
 * Update the crc value with new data.
 *
 * \param crc      The current crc value.
 * \param data     Pointer to a buffer of \a data_len bytes.
 * \param data_len Number of bytes in the \a data buffer.
 * \return         The updated crc value.
 *****************************************************************************
crc_t crc_update(crc_t crc, const unsigned char *data, size_t data_len)
{
    unsigned int i;
    bool bit;
    unsigned char c;

    while (data_len--) {
        c = *data++;
        for (i = 0; i < 8; i++) {
            bit = crc & 0x40;
            crc = (crc << 1) | ((c >> (7 - i)) & 0x01);
            if (bit) {
                crc ^= 0x37;
            }
        }
        crc &= 0x7f;
    }
    return crc & 0x7f;
}
</pre>
     */
    public static final byte bbb_update(byte crc, final byte[] data, int data_len)
        {
        for(int data_index = 0; data_len-- > 0; )
            {
            final byte c = data[data_index++];
            for(int i = 0; i < 8; ++i)
                {
                final boolean bit = (0 != (crc & 0x40));
                crc = (byte)((crc << 1) | ((c >> (7-i)) & 1));
                if(bit) { crc ^= 0x37; }
                }
            crc &= 0x7f;
            }
        return((byte) (crc & 0x7f));
        }

    /**Finish
<pre>
 * Calculate the final crc value.
 *
 * \param crc  The current crc value.
 * \return     The final crc value.
 *****************************************************************************
crc_t crc_finalize(crc_t crc)
{
    unsigned int i;
    bool bit;

    for (i = 0; i < 7; i++) {
        bit = crc & 0x40;
        crc = (crc << 1) | 0x00;
        if (bit) {
            crc ^= 0x37;
        }
    }
    return (crc ^ 0x00) & 0x7f;
}
</pre>
     */
    public static byte bbb_finalize(byte crc)
        {
        for(int i = 0; i < 7; ++i)
            {
            final boolean bit = (0 != (crc & 0x40));
            crc <<= 1;
            if(bit) { crc ^= 0x37; }
            }
        return((byte) (crc & 0x7f));
        }

    }