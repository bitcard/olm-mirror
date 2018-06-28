#include "olm/pk.h"
#include "olm/crypto.h"
#include "olm/olm.h"

#include "unittest.hh"

#include <iostream>

int main() {


{ /* Encryption Test Case 1 */

TestCase test_case("Public Key Encryption/Decryption Test Case 1");

std::uint8_t decryption_buffer[olm_pk_decryption_size()];
OlmPkDecryption *decryption = olm_pk_decryption(decryption_buffer);

std::uint8_t alice_private[32] = {
    0x77, 0x07, 0x6D, 0x0A, 0x73, 0x18, 0xA5, 0x7D,
    0x3C, 0x16, 0xC1, 0x72, 0x51, 0xB2, 0x66, 0x45,
    0xDF, 0x4C, 0x2F, 0x87, 0xEB, 0xC0, 0x99, 0x2A,
    0xB1, 0x77, 0xFB, 0xA5, 0x1D, 0xB9, 0x2C, 0x2A
};

const std::uint8_t *alice_public = (std::uint8_t *) "hSDwCYkwp1R0i33ctD73Wg2/Og0mOBr066SpjqqbTmoK";

std::uint8_t bob_private[32] = {
    0x5D, 0xAB, 0x08, 0x7E, 0x62, 0x4A, 0x8A, 0x4B,
    0x79, 0xE1, 0x7F, 0x8B, 0x83, 0x80, 0x0E, 0xE6,
    0x6F, 0x3B, 0xB1, 0x29, 0x26, 0x18, 0xB6, 0xFD,
    0x1C, 0x2F, 0x8B, 0x27, 0xFF, 0x88, 0xE0, 0xEB
};

const std::uint8_t *bob_public = (std::uint8_t *) "3p7bfXt9wbTTW2HC7OQ1Nz+DQ8hbeGdNrfx+FG+IK08K";

std::uint8_t pubkey[::olm_pk_key_length()];

olm_pk_generate_key(
    decryption,
    pubkey, sizeof(pubkey),
    alice_private, sizeof(alice_private)
);

assert_equals(alice_public, pubkey, olm_pk_key_length());

std::uint8_t encryption_buffer[olm_pk_encryption_size()];
OlmPkEncryption *encryption = olm_pk_encryption(encryption_buffer);

olm_pk_encryption_set_recipient_key(encryption, pubkey, sizeof(pubkey));

const size_t plaintext_length = 14;
const std::uint8_t *plaintext = (std::uint8_t *) "This is a test";

size_t ciphertext_length = olm_pk_ciphertext_length(encryption, plaintext_length);
std::uint8_t *ciphertext_buffer = (std::uint8_t *) malloc(ciphertext_length);

std::uint8_t output_buffer[olm_pk_mac_length(encryption)];
std::uint8_t ephemeral_key[olm_pk_key_length()];

olm_pk_encrypt(
    encryption,
    plaintext, plaintext_length,
    ciphertext_buffer, ciphertext_length,
    output_buffer, sizeof(output_buffer),
    ephemeral_key, sizeof(ephemeral_key),
    bob_private, sizeof(bob_private)
);

assert_equals(bob_public, ephemeral_key, olm_pk_key_length());

size_t max_plaintext_length = olm_pk_max_plaintext_length(decryption, ciphertext_length);
std::uint8_t *plaintext_buffer = (std::uint8_t *) malloc(max_plaintext_length);

olm_pk_decrypt(
    decryption,
    ephemeral_key, sizeof(ephemeral_key),
    output_buffer, sizeof(output_buffer),
    ciphertext_buffer, ciphertext_length,
    plaintext_buffer, max_plaintext_length
);

assert_equals(plaintext, plaintext_buffer, plaintext_length);

free(ciphertext_buffer);
free(plaintext_buffer);

}

}
