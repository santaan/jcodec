package org.jcodec.samples.transcode;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;

import org.jcodec.codecs.h264.H264Decoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.io.model.Frame;
import org.jcodec.common.Codec;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.Format;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture8Bit;
import org.jcodec.common.tools.MainUtils.Cmd;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;

class Avc2png extends ToImgTranscoder {

    @Override
    protected VideoDecoder getDecoder(Cmd cmd, DemuxerTrack inTrack, ByteBuffer firstFrame) {
        return H264Decoder.createH264DecoderFromCodecPrivate(inTrack.getMeta().getCodecPrivate());
    }

    @Override
    protected DemuxerTrack getDemuxer(Cmd cmd, SeekableByteChannel source) throws IOException {
        return new MP4Demuxer(source).getVideoTrack();
    }

    @Override
    protected Frame decodeFrame(VideoDecoder decoder, Picture8Bit target1, ByteBuffer pkt) {
        return ((H264Decoder) decoder).decodeFrame8BitFromNals(H264Utils.splitFrame(pkt), target1.getData());
    }

    @Override
    public Set<Format> inputFormat() {
        return TranscodeMain.formats(Format.MOV);
    }

    @Override
    public Set<Codec> inputVideoCodec() {
        return TranscodeMain.codecs(Codec.H264);
    }

    @Override
    protected Packet nextPacket(DemuxerTrack inTrack) throws IOException {
        return inTrack.nextFrame();
    }
}