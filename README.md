Welcome to Stealthosaurus!

This program will hide any basic character array (supports ascii values 33 through 126, so please remove white space, carriage returns etc.) in
an image of either PGM or PPM format in byte indexes offset by a chosen value N.  The image must be large enough that N*character array length 
does not exceed its size in bytes.

Directions:
1.Run Stealthosaurus and choose 'norm' mode (debug mode loads some pre-filled parameters behind the scenes)
2.Choose the operation you wish to perform.  If you wish to hide a message, choose encode.  If you wish to
retrieve a hidden message, choose decode.
IF ENCODE:
  3. Enter 'charbyn' (microbits is an experimental algorithm for hiding binary data in an image and is not fully functional)
  4. Enter an integer N.  This value will cause the encoding to store character values at offset image byte indexes, meaning
     that a greater N should lead to a more difficult to detect obfuscation.  N*character array length must be less than or equal
     to the image's size in bytes, however.
  5. Enter the image format of the image in which you wish to hide the message
  6. Enter the image width
  7. Enter the image height
  8. Enter the length (in characters) of the message to be encoded
  9. Enter the name of the image file (images must be placed in the "assets" directory, and must be in raw data form with no header. Ex. photo.dat)
  10. Enter the name of the character message file (messages must be placed in the "message" directory Ex. mySecret.txt)
IF DECODE:
  3. Enter 'charbyn' (microbits is an experimental algorithm for hiding binary data in only the LSB of the of an image's bytes and is not fully functional)
  4. Enter the image format of the image in which you have hidden the message
  5. Enter the image width
  6. Enter the image height
  7. Enter the name of the image file (images must be placed in the "assets" directory, and must be in raw data form with no header. Ex. photo.dat)

Encoded images will be placed in the assets directory, with a name prefix "result_encoded_charbyn_".  To view the encoded images, open them in
a text editor and add the appropriate header, then open in GIMP.

Decoded messages will be place in the assets directory, with a name prefix "result_secret_in_".  They can be viewed with any text editor and
should match the original character array.

KNOWN ISSUES:
Last time I checked, and this was a while ago, decoding doesn't quite work for some reason, even for charbyn encoding.  It used to work... 
