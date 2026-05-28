/*
 * Copyright (c) 2001-2006 MUSIC TECHNOLOGY GROUP (MTG)
 *                         UNIVERSITAT POMPEU FABRA
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

#ifndef ChordExtractor_hxx
#define ChordExtractor_hxx

#include "ChordCorrelator.hxx"
#include "CircularPeakPicking.hxx"
#include "CircularPeaksToPCP.hxx"
#include "CircularPeakTunner.hxx"
#include "ConstantQFolder.hxx"
#include "ConstantQTransform.hxx"
#include "FourierTransform.hxx"
#include "InstantTunningEstimator.hxx"
#include "PCPSmother.hxx"

// For logging purposes
#include "../jniUtils.h"

namespace Simac
{

  class ChordExtractor
  {
    double _sparseConstantQKernelThreshold;
    ConstantQTransform _constantQTransform;
    ConstantQFolder _constantQFolder;
    FourierTransform _fourierTransform;
    CircularPeakPicking _circularPeakPicking;
    InstantTunningEstimator _instantTunningEstimator;
    CircularPeakTunner _circularPeakTunner;
    CircularPeaksToPCP _circularPeaksToPCP;
    PCPSmother _filter;
    ChordCorrelator _chordCorrelator;
    bool _tunningEnabled;
    bool _peakWindowingEnabled;
    double _squaredRootEnergy;
  public:
    static double maximumFrequency(double sampleRate) { return sampleRate/2.1; } // Just below nyquist
    typedef float * AudioFrame;

  ChordExtractor(unsigned sampleRate, double minimumFrequency, unsigned binsPerOctave, double instant_tunning_estimator_inertia, double sparse_constant_qkernel_threshold,
		 int correlationThreshold, double componentThreshold)
    : _sparseConstantQKernelThreshold(sparse_constant_qkernel_threshold)
      , _constantQTransform(sampleRate, minimumFrequency, maximumFrequency(sampleRate), binsPerOctave)
      , _constantQFolder(_constantQTransform.getK(), binsPerOctave)
      , _fourierTransform(_constantQTransform.getfftlength(),1,0)
      , _circularPeakPicking(binsPerOctave, /*scaling factor*/ 12.0/binsPerOctave)
      , _instantTunningEstimator(instant_tunning_estimator_inertia)
      , _circularPeakTunner(/*reference tunning*/ 0.0)
      , _chordCorrelator(correlationThreshold, componentThreshold)
      , _filter(0.7)
      , _tunningEnabled(true)
      , _peakWindowingEnabled(true)
      {
	_constantQTransform.sparsekernel(_sparseConstantQKernelThreshold);
	if (_peakWindowingEnabled)
	  _circularPeaksToPCP.activateWindowing();
      }

    // Accessors
    void setFilterInertia(double inertia){ _filter.inertia(inertia);}
    void setEnableTunning(bool tunningEnabled=true)  { _tunningEnabled=tunningEnabled; }
    void setEnablePeakWindowing(bool peakWindowingEnabled=true)  { _peakWindowingEnabled=peakWindowingEnabled; }

    unsigned getFrameSize() const {return _constantQTransform.getfftlength();}

    void doIt(const AudioFrame & input /*, CLAM::TData & currentTime*/)
    {
      _squaredRootEnergy = 0.0;
      for (unsigned i=0; i < getFrameSize(); i++)
	_squaredRootEnergy += input[i]*input[i];

      _fourierTransform.doIt(input);
      _constantQTransform.doIt(_fourierTransform.spectrum());
      _constantQFolder.doIt(_constantQTransform.constantQSpectrum());
      _circularPeakPicking.doIt(_constantQFolder.chromagram());
      _instantTunningEstimator.doIt(_circularPeakPicking.output());
      _circularPeakTunner.doIt(_instantTunningEstimator.output().first, _circularPeakPicking.output());

      if (_tunningEnabled)
	_circularPeaksToPCP.doIt(_circularPeakTunner.output());
      else
	_circularPeaksToPCP.doIt(_circularPeakPicking.output());

      _filter.doIt(_circularPeaksToPCP.output());
      _chordCorrelator.doIt(_filter.output());
    }
	
    double tunning() const {return _instantTunningEstimator.output().first; }
    double tunningStrength() const {return _instantTunningEstimator.output().second; }
    std::pair<double,double> instantTunning() const {return _instantTunningEstimator.instantTunning(); }
    double energy() const {return _squaredRootEnergy; }

    // Clam uses an enum {G,Ab,A,Bb,B,C,Db,D,Eb,E,F,Gb};
    // while Tabbeo uses {C,Db,D,Eb,E,F,Gb,G,Ab,A,Bb,B};
    // We need to adjust the offset
    int convertClamRootIndexToTabbeo(int clamRootIndex)
    {
      int tabbeoRootIndex = clamRootIndex - 5; //Offset of 5
      if(tabbeoRootIndex < 0)
	tabbeoRootIndex += 12;
      return tabbeoRootIndex;
    }
    
    int EncodeChord(int clamRootIndex, int mode)
    {
      int code = 0;

      code = ((convertClamRootIndexToTabbeo(clamRootIndex) & 0xF) << 4)
	| ((mode & 0xF));
      
      return code;
    }

    void createCandidateChordsArray(std::vector<int>& chordCandidates, double intensityThreshold)
    {
      // We set a intensity minimun. This will be handled by the correlator
      if(_chordCorrelator.getMaxIntensity() < intensityThreshold) return;
      
      
      const std::vector<ChordCorrelator::Chord*>& detectedChords = _chordCorrelator.getDetectedChords();
      
      for(ChordCorrelator::Chord* chord : detectedChords){
	chordCandidates.push_back(EncodeChord(chord->pitch,
					      chord->mode));
      }
    }
  };
}

#endif//ChordExtractor

