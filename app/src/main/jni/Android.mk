# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := libfftw
LOCAL_CFLAGS    := -Werror -fexceptions -g\
		-I$(LOCAL_PATH)/fftw \
		-I$(LOCAL_PATH)/fftw/api \
		-I$(LOCAL_PATH)/fftw/kernel \
		-I$(LOCAL_PATH)/fftw/dft \
		-I$(LOCAL_PATH)/fftw/rdft \
		-I$(LOCAL_PATH)/fftw/reodft \
		-I$(LOCAL_PATH)/fftw/simd \
		-I$(LOCAL_PATH)/fftw/rdft/simd \
		-I$(LOCAL_PATH)/fftw/rdft/scalar \
		-I$(LOCAL_PATH)/fftw/dft/simd \
		-I$(LOCAL_PATH)/fftw/dft/scalar

LOCAL_SRC_FILES := $(shell cd $(LOCAL_PATH); find ./fftw/ -type f -name '*.c'; find ./fftw/ -type f -name '*.cpp')

include $(BUILD_STATIC_LIBRARY)


#libaubio module
include $(CLEAR_VARS)
LOCAL_MODULE    := libaubio
LOCAL_CFLAGS    := -Werror -g\
		-I$(LOCAL_PATH)/aubio \
		-I$(LOCAL_PATH)/aubio/ext \
		-I$(LOCAL_PATH)/aubio/src \
		-I$(LOCAL_PATH)/libsamplerate \
		-I$(LOCAL_PATH)/fftw \
		-I$(LOCAL_PATH)/libsndfile \
		-I$(LOCAL_PATH)/include

LOCAL_SRC_FILES := $(shell cd $(LOCAL_PATH); find ./aubio/ -type f -name '*.c'; find ./aubio/ -type f -name '*.cpp';)
LOCAL_STATIC_LIBRARIES := libfftw libsndfile libsamplerate libfftw

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := Detector
LOCAL_CFLAGS    := -Werror -g -std=c++11\
		-I$(LOCAL_PATH)/aubio \
		-I$(LOCAL_PATH)/aubio/src \
		-I$(LOCAL_PATH)/fftw \
		-DUSE_FFTW3\
		-I$(LOCAL_PATH)/fftw/api \

LOCAL_STATIC_LIBRARIES := libaubio libfftw
LOCAL_SRC_FILES := PitchDetector.cpp ChordDetector.cpp clam/ChordCorrelator.cxx clam/FourierTransform.cxx clam/ConstantQFolder.cxx clam/ConstantQTransform.cxx jniUtils.cpp main.cpp
LOCAL_LDLIBS := -Lbuild/platforms/android-1.5/arch-arm/usr/lib -llog -lm

include $(BUILD_SHARED_LIBRARY)




