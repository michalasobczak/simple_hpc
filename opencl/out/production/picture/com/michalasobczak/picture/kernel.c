const sampler_t samplerIn = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP | CLK_FILTER_NEAREST;

__kernel void sampleKernel(__read_only  image2d_t sourceImage,
                           __write_only image2d_t targetImage,
                           float factor) {
  int gidX = get_global_id(0);
  int gidY = get_global_id(1);
  //printf("%u, %u\n", gidX, gidY);
  int w = get_image_width(sourceImage);
  int h = get_image_height(sourceImage);
  int2 posIn  = {gidX, gidY};
  int2 posOut = {gidX/factor, gidY/factor};
  uint4 pixel = read_imageui(sourceImage, samplerIn, posIn);
  write_imageui(targetImage, posOut, pixel);
}
